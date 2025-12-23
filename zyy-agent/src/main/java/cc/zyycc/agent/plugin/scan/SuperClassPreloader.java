package cc.zyycc.agent.plugin.scan;

import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperClassPreloader {
    private static final ExecutorService SCAN_POOL =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void init() {
        List<Path> jarFiles = new ArrayList<>();

        Path pluginsDir = Paths.get(VersionInfo.WORKING_DIR, "plugins");
        if (!pluginsDir.toFile().exists()) {
            notifyReady();
            return;
        }
        try {
            Files.walk(pluginsDir)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .forEach(jarFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CountDownLatch latch = new CountDownLatch(jarFiles.size());
        long t0 = System.nanoTime();

        JarScanInfo jarScanInfo = new JarScanInfo();
        for (Path jar : jarFiles) {
            jarScanInfo.createScanUnit(jar);
        }
        boolean scan = !jarScanInfo.compareHash();

        if (!scan) {
            jarScanInfo.loadCache();
        } else {
            jarScanInfo.removeFile();
            for (JarScanUnit unit : jarScanInfo.getJarScan()) {
                SCAN_POOL.submit(() -> {
                    try {
                        preloadNmsSuperClasses(unit);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            try {
                latch.await();
                JarScanInfo.printFieldCount("1Field现在数量 ");
                JarScanInfo.printMethodCount("1Method现在数量 ");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long t1 = System.nanoTime();
        System.out.println("一阶段扫描耗时: " + (t1 - t0) / 1_000_000 + " ms");
        if (scan) {
            runSecondPhase();
        }

        long t2 = System.nanoTime();
        System.out.println("二阶段扫描耗时: " + (t2 - t1) / 1_000_000 + " ms");
        System.out.println("总: " + (t2 - t0) / 1_000_000 + " ms");
        JarScanInfo.printFieldCount("2Field现在数量 ");
        JarScanInfo.printMethodCount("2Method现在数量 ");
        notifyReady();

        runThirdPhase();
        JarScanInfo.printFieldCount("3Field现在数量 ");
        JarScanInfo.printMethodCount("3Method现在数量 ");
        if (scan) {
            JarScanInfo.saveCache();
        }
    }

    public static void runSecondPhase() {
        for (String className : JarScanInfo.getClassKey()) {
            SuperClassHelper.checkOrRemove(className);
        }
    }

    public static void runThirdPhase() {
        for (String className : JarScanInfo.getClassKey()) {
            SuperClassHelper.checkOrRemove1(className);
        }
    }


    public static void preloadNmsSuperClasses(JarScanUnit unit) {
        try (FileSystem fs = FileSystems.newFileSystem(unit.getJar(), null)) {
            for (Path root : fs.getRootDirectories()) {
                Files.walk(root)
                        .filter(p -> p.toString().endsWith(".class"))
                        .forEach(clazz ->
                                scanClass(clazz, unit.classes, unit.fields, unit.methods));
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
            cr.accept(new ClassVisitor(Opcodes.ASM9) {

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


    private static void notifyReady() {
        try {
            Class<?> bridge = Class.forName("cc.zyycc.common.bridge.PreScanBridge", false, ClassLoader.getSystemClassLoader());
            Method m = bridge.getMethod("notifyReady");
            m.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
