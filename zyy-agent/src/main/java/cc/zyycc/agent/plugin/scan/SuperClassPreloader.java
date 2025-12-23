package cc.zyycc.agent.plugin;

import cc.zyycc.agent.transformer.scan.JarScanUnit;
import cc.zyycc.common.VersionInfo;
import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.cache.MappingCacheManager;
import cc.zyycc.remap.cache.MappingCache;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperClassPreloader {


    private static final ExecutorService SCAN_POOL =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static final Map<String, String> CACHE_CLASS_CACHE = new ConcurrentHashMap<>();


    public static final Map<String, Set<String>> fieldsCaches = new ConcurrentHashMap<>();
    public static final Map<String, Set<String>> methodsCaches = new ConcurrentHashMap<>();


    public static final MappingCache<BaseEntry> SCAN = MappingCacheManager.PLUGINS_CLASS_SCAN;

    public static void init() {


        List<Path> jarFiles = new ArrayList<>();
        Path pluginsDir = Paths.get(VersionInfo.WORKING_DIR, "plugins");
        try {
            Files.walk(pluginsDir)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .forEach(jarFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CountDownLatch latch = new CountDownLatch(jarFiles.size());
        long t0 = System.nanoTime();
        for (Path jar : jarFiles) {
            SCAN_POOL.submit(() -> {
                try {
                    byte[] bytes = Files.readAllBytes(jar);
                    if (!hashCompare(bytes)) {
                        JarScanUnit jarScanUnit = new JarScanUnit(jar);
                        preloadNmsSuperClasses(jarScanUnit);
                        saveHash(bytes);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            System.out.println("1Method现在数量" + methodsCaches.size());
            System.out.println("1Field现在数量" + fieldsCaches.size());
            System.out.println("1Class现在数量" + CACHE_CLASS_CACHE.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long t1 = System.nanoTime();
        System.out.println("一阶段扫描耗时: " + (t1 - t0) / 1_000_000 + " ms");
        runSecondPhase();
        long t2 = System.nanoTime();
        System.out.println("二阶段扫描耗时: " + (t2 - t1) / 1_000_000 + " ms");
        System.out.println("总: " + (t2 - t0) / 1_000_000 + " ms");
        System.out.println("2Method现在数量" + methodsCaches.size());
        System.out.println("2Field现在数量" + fieldsCaches.size());
        System.out.println("2Class现在数量" + CACHE_CLASS_CACHE.size());


        try {
            Class<?> bridge = Class.forName("cc.zyycc.common.bridge.PreScanBridge", false, ClassLoader.getSystemClassLoader());
            Method m = bridge.getMethod("notifyReady");
            m.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        SuperClassPreloader.runThirdPhase();

        for (Map.Entry<String, Set<String>> entry : fieldsCaches.entrySet()) {

            System.out.println("当前class" + entry.getKey());

            entry.getValue().forEach(fieldName -> {
                System.out.println(fieldName);
            });
        }


        System.out.println("3Method现在数量" + methodsCaches.size());
        System.out.println("3Field现在数量" + fieldsCaches.size());
    }

    public static void runSecondPhase() {
        for (String className : CACHE_CLASS_CACHE.keySet()) {
            SuperClassHelper.checkOrRemove(className);
        }
    }

    public static void runThirdPhase() {
        for (String className : CACHE_CLASS_CACHE.keySet()) {
            SuperClassHelper.checkOrRemove2(className);
        }
    }


    public static void preloadNmsSuperClasses(JarScanUnit unit) {

        try (FileSystem fs = FileSystems.newFileSystem(unit.getJar(), null)) {
            for (Path root : fs.getRootDirectories()) {
                Files.walk(root)
                        .filter(p -> p.toString().endsWith(".class"))
                        .forEach(clazz ->
                                scanClass(clazz, unit.cacheClasses, unit.fieldsCaches, unit.methodsCaches));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void scanClass(Path clazz, Map<String, String> superClassCache,
                                  Map<String, Set<String>> fieldsCache,
                                  Map<String, Set<String>> methodsCache
    ) {
        try {
            byte[] bytes = Files.readAllBytes(clazz);
            ClassReader cr = new ClassReader(bytes);
            cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {

                private String thisClass;
                private boolean needScan = false;

                @Override
                public void visit(int version, int access, String name,
                                  String signature, String superName, String[] interfaces) {

                    this.thisClass = name;
//
//                    needScan = !name.startsWith("java/")
//                            && !name.startsWith("javax/")
//                            && !name.startsWith("sun/")
//                            && !name.startsWith("com/google/")
//                            && !name.startsWith("com/mojang/");

                    if (superName != null && !superName.startsWith("java/")) {
                        needScan = true;
                        superClassCache.put(name, superName);
                    }
                    if (needScan) {
                        fieldsCache.putIfAbsent(name, ConcurrentHashMap.newKeySet());
                        methodsCache.putIfAbsent(name, ConcurrentHashMap.newKeySet());
                    }
                }


                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if (needScan) {
                        fieldsCache.get(thisClass).add(name);
                    }

                    return null;
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (needScan) {
                        methodsCache.get(thisClass).add(name + '#' + descriptor);
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        } catch (Throwable ignored) {
        }
    }

    public static void put(String name, String superName) {
        if (name == null || superName == null) return;
        CACHE_CLASS_CACHE.putIfAbsent(name, superName);
    }

    public static String get(String name) {
        return CACHE_CLASS_CACHE.get(name);
    }


    /**
     * 往上追一层，
     */
    public static String getRootSuper(String name) {
        String current = name;
        String parent = CACHE_CLASS_CACHE.get(current);
        while (parent != null) {
            current = parent;
            parent = CACHE_CLASS_CACHE.get(current);
        }
        return current;
    }

    private static String extractPluginName(ProtectionDomain pd) {
        try {
            CodeSource src = pd.getCodeSource();
            if (src == null || src.getLocation() == null) {
                return "unknown";
            }
            String path = src.getLocation().getPath();

            String fileName = new File(path).getName();

            return fileName.replace(".jar", "");
        } catch (Throwable e) {
            return "unknown";
        }
    }

    private static void saveHash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            String hash = Base64.getEncoder().encodeToString(md.digest());
            SCAN.addSuccess(hash);
        } catch (NoSuchAlgorithmException ignored) {
        }
    }


    private static boolean hashCompare(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            String hash = Base64.getEncoder().encodeToString(md.digest());

            return SCAN.hasSuccessCache(new BaseEntry(hash));
        } catch (NoSuchAlgorithmException ignored) {

        }
        return false;

    }

}
