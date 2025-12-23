package cc.zyycc.forge;


import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.bridge.InstrumentationBridge;
import cc.zyycc.common.bridge.PreScanBridge;
import cc.zyycc.common.util.ServerUtil;
import cpw.mods.modlauncher.Launcher;
import net.minecraft.command.Commands;
import org.apache.logging.log4j.util.StackLocator;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.*;
import java.util.stream.Collectors;


public class MainForge {
    public static void startForgeServer(String[] args, boolean canStart) {


//      classPath.add(spigotJar);

//        LibManager.addLibrary(libraries);

        //LibManager.addLibrary(Arrays.asList(libraries), Arrays.asList("log4j-slf4j18-impl"));
//        classPath.add(Paths.get("libraries/org/ow2/asm/asm/9.1/asm-9.1.jar"));
//        classPath.add(Paths.get("libraries/org/ow2/asm/asm-commons/9.1/asm-commons-9.1.jar"));
//        classPath.add(Paths.get("libraries/org/ow2/asm/asm-tree/9.1/asm-tree-9.1.jar"));
//        classPath.add(Paths.get("libraries/org/ow2/asm/asm-util/9.1/asm-util-9.1.jar"));
//        classPath.add(Paths.get("libraries/net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar"));
//        classPath.add(Paths.get("libraries/org/spongepowered/mixin/0.8.4/mixin-0.8.4.jar"));

        //LibManager.addLibrary("libraries/org/ow2/asm/asm/9.1/asm-9.1.jar");
//        LibManager.addLibrary("libraries/org/ow2/asm/asm-commons/9.1/asm-commons-9.1.jar");
//        LibManager.addLibrary("libraries/org/ow2/asm/asm-tree/9.1/asm-tree-9.1.jar");
//        LibManager.addLibrary("libraries/org/ow2/asm/asm-util/9.1/asm-util-9.1.jar");
//        LibManager.addLibrary("libraries/org/ow2/asm/asm-analysis/9.1/asm-analysis-9.1.jar");
//        LibManager.addLibrary("libraries/cpw/mods/modlauncher/8.1.3/modlauncher-8.1.3.jar");
//        LibManager.addLibrary("libraries/cpw/mods/grossjava9hacks/1.3.3/grossjava9hacks-1.3.3.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/accesstransformers/3.0.1/accesstransformers-3.0.1.jar");
//        LibManager.addLibrary("libraries/org/antlr/antlr4-runtime/4.9.1/antlr4-runtime-4.9.1.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/eventbus/4.0.0/eventbus-4.0.0.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/forgespi/3.2.0/forgespi-3.2.0.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/coremods/4.0.6/coremods-4.0.6.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/unsafe/0.2.0/unsafe-0.2.0.jar");
//        LibManager.addLibrary("libraries/com/electronwill/night-config/core/3.6.3/core-3.6.3.jar");
//        LibManager.addLibrary("libraries/com/electronwill/night-config/toml/3.6.3/toml-3.6.3.jar");
//        LibManager.addLibrary("libraries/org/jline/jline/3.12.1/jline-3.12.1.jar");
//        LibManager.addLibrary("libraries/org/apache/maven/maven-artifact/3.6.3/maven-artifact-3.6.3.jar");
//        LibManager.addLibrary("libraries/net/jodah/typetools/0.8.3/typetools-0.8.3.jar");
//        LibManager.addLibrary("libraries/org/apache/logging/log4j/log4j-api/2.15.0/log4j-api-2.15.0.jar");
//        LibManager.addLibrary("libraries/org/apache/logging/log4j/log4j-core/2.15.0/log4j-core-2.15.0.jar");
//        // LibManager.addLibrary("libraries/org/apache/logging/log4j/log4j-slf4j18-impl/2.15.0/log4j-slf4j18-impl-2.15.0.jar");
//        LibManager.addLibrary("libraries/net/minecrell/terminalconsoleappender/1.2.0/terminalconsoleappender-1.2.0.jar");
//        LibManager.addLibrary("libraries/net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar");
//        LibManager.addLibrary("libraries/org/spongepowered/mixin/0.8.4/mixin-0.8.4.jar");
//        LibManager.addLibrary("libraries/net/minecraftforge/nashorn-core-compat/15.1.1.1/nashorn-core-compat-15.1.1.1.jar");
//        LibManager.addLibrary("libraries/net/minecraft/server/1.16.5-20210115.111550/server-1.16.5-20210115.111550-extra.jar");

//        LibManager.addLibrary(extraLibraries);
//        LibManager.generateAppClassLoader();
//
//        classPath.add(new File("libraries/com/google/guava/guava/25.1-jre/guava-25.1-jre.jar").toPath());
//
//        classPath.add(forgeJar);
//        classPath.add(Paths.get(VersionInfo.WORKING_DIR, "zyyaruzi.jar"));
//
//        URL[] urls = classPath.stream().map(path -> {
//            try {
//                return path.toUri().toURL();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }).toArray(URL[]::new);

//        ClassLoader myLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

//        ClassLoader myLoader = MainForge.class.getClassLoader();
//
//        LoaderHandler.addToPaths(myLoader, classPath);

//        BridgeHolder.INSTANCE.setClassLoader(myLoader);

        //kissAllFromJar(forgeJar, myLoader, forge());

        //definePackageFromJarManifest(myLoader, forgeJar);


        createDefaultConfig();

        redefineListener();

        if(canStart){
            while (!PreScanBridge.ready) {
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }


        Launcher.main(collectArgs(args));

    }

    public static void redefineListener() {
        System.out.println("listener start");
        new Thread(() -> {
            Path file = Paths.get("agent", "agent_command.txt");
            while (true) {
                if (Files.exists(file)) {
                    try {
                        List<String> lines = Files.readAllLines(file);
                        List<String> updated = new ArrayList<>();
                        for (String line : lines) {
                            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                                updated.add(line);
                                continue;
                            }
                            if (line.startsWith("redefine ")) {
                                String cls = line.substring(9).trim();

                                Path path = Paths.get("agent", cls.replace('.', '/') + ".class");

                                if (!path.toFile().exists()) {
                                    System.err.println("Class " + cls + " not found");
                                    updated.add("# " + line + "    [FAILED: not found]");
                                } else {
                                    if(redefine(cls, path)){
                                        updated.add("# " + line + "    [OK]");
                                    }else {
                                        updated.add("# " + line + "    [FAILED]");
                                    }
                                }
                            }
                        }
                        Files.write(file, updated, StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (Exception ignored) {
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    public static boolean redefine(String className, Path bytesPath) {
        try {
            Instrumentation inst = InstrumentationBridge.getInst();

            for (Class<?> c : inst.getAllLoadedClasses()) {
                if (c.getName().equals(className.replace('/', '.'))) {
                    byte[] newBytes = Files.readAllBytes(bytesPath);
//                    inst.retransformClasses(c);
                    inst.redefineClasses(new ClassDefinition(c, newBytes));
                    System.out.println("[Agent] redefined " + className);
                    return true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static void createDefaultConfig() {
        Path path = Paths.get(VersionInfo.WORKING_DIR, "config");

        ServerUtil.createFileIfAbsent(path.resolve("fml.toml"),
                "# Enable forge global version checking\n" +
                        "versionCheck = true\n" +
                        "# does the splashscreen run\n" +
                        "splashscreen = true\n" +
                        "defaultConfigPath = \"defaultconfigs\"\n" +
                        "# max threads for parallel loading : -1 uses Runtime#availableProcessors\n" +
                        "maxThreads = -1\n" +
                        "\n");

        ServerUtil.createFileIfAbsent(path.resolve("forge-common.toml"),
                "\n" +
                        "#General configuration settings\n" +
                        "[general]\n" +
                        "\t#Defines a default world type to use. The vanilla default world type is represented by 'default'.\n" +
                        "\t#The modded world types are registry names which should include the registry namespace, such as 'examplemod:example_world_type'.\n" +
                        "\tdefaultWorldType = \"default\"\n" +
                        "\n");


    }

    public static Path patchForgeManifest(Path forgeJar) throws Exception {
        // 1. 读取 MANIFEST
        Manifest manifest;
        try (JarFile jarFile = new JarFile(forgeJar.toFile())) {
            manifest = jarFile.getManifest();
        }

        if (manifest == null) {
            throw new IllegalStateException("没有找到 MANIFEST.MF！");
        }

        // 2. 创建临时 JAR 文件
        Path tempJar = Paths.get(VersionInfo.WORKING_DIR, "nashimanifest.jar");
        //  tempJar.toFile().deleteOnExit(); // 程序结束后自动清除
        Files.createDirectories(tempJar.getParent());

        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(tempJar), manifest)) {
            // 写入 minecraftmod.toml
            jos.putNextEntry(new JarEntry("minecraftmod.toml"));
            String str = "modLoader=\"minecraft\"\n" +
                    "loaderVersion=\"1\"\n" +
                    "license=\"Mojang Studios, All Rights Reserved\"\n" +
                    "[[mods]]\n" +
                    "    modId=\"minecraft\"\n" +
                    "    version=\"${global.mcVersion}\"\n" +
                    "    displayName=\"Minecraft\"\n" +
                    "    logoFile=\"mcplogo.png\"\n" +
                    "    credits=\"Mojang, deobfuscated by MCP\"\n" +
                    "    authors=\"MCP: Searge,ProfMobius,IngisKahn,Fesh0r,ZeuX,R4wk,LexManos,Bspkrs\"\n" +
                    "    description='''\n" +
                    "    Minecraft, decompiled and deobfuscated with MCP technology\n" +
                    "    '''";


            jos.write(str.getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();


            jos.putNextEntry(new JarEntry("META-INF/mods.toml"));

            String modsToml = "modLoader=\"javafml\"\n" +
                    "loaderVersion=\"[24,]\"\n" +
                    "issueTrackerURL=\"http://www.minecraftforge.net/\"\n" +
                    "logoFile=\"forge_logo.png\"\n" +
                    "license=\"LGPL v2.1\"\n" +
                    "\n" +
                    "[[mods]]\n" +
                    "modId=\"forge\"\n" +
                    "# We use the global forge version\n" +
                    "version=\"${global.forgeVersion}\"\n" +
                    "updateJSONURL=\"https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json\"\n" +
                    "displayName=\"Forge\"\n" +
                    "credits=\"Anyone who has contributed on Github and supports our development\"\n" +
                    "authors=\"LexManos,cpw\"\n" +
                    "description='''\n" +
                    "    Forge, a broad compatibility API.\n" +
                    "    '''\n";


            jos.write(modsToml.getBytes(StandardCharsets.UTF_8));

            jos.closeEntry();

            jos.putNextEntry(new JarEntry("META-INF/mods.toml.sha1"));
            jos.write("".getBytes(StandardCharsets.UTF_8));
            jos.write("".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();

        }

        return tempJar;
    }


    private static void definePackageManually(ClassLoader loader, Attributes attrs) throws Exception {
        Method definePackage = ClassLoader.class.getDeclaredMethod("definePackage",
                String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, URL.class);
        definePackage.setAccessible(true);

        String specTitle = attrs.getValue(Attributes.Name.SPECIFICATION_TITLE);
        String specVersion = attrs.getValue(Attributes.Name.SPECIFICATION_VERSION);
        String specVendor = attrs.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        String implTitle = attrs.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        String implVersion = attrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        String implVendor = attrs.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        if (specTitle == null) specTitle = "Forge Spec";
        if (specVersion == null) specVersion = "1.0";
        if (specVendor == null) specVendor = "Forge Team";
        if (implTitle == null) implTitle = "Forge Launcher";
        if (implVersion == null) implVersion = "36.2.34";
        if (implVendor == null) implVendor = "Forge Team";

        definePackage.invoke(loader, "net.minecraftforge.fml.loading",
                specTitle, specVersion, specVendor,
                implTitle, implVersion, implVendor, null);

    }


//    public static void registerLaunchHandlerToSystem(Path forgeJar) throws Exception {
//        String className = "net.minecraftforge.fml.loading.FMLServerLaunchProvider";
//
//        ClassLoader sysLoader = ClassLoader.getSystemClassLoader();
//        Class<?> impl;
//
//        try {
//            // ✅ 优先尝试加载，避免重复 define
//            impl = Class.forName(className, false, sysLoader);
//            System.out.println("⚠ 已加载类，跳过 define: " + className);
//        } catch (ClassNotFoundException e) {
//            byte[] bytes = readClassBytes(forgeJar, className);
//            impl = defineClassToSystem(className, bytes);
//            System.out.println("✅ define 到系统类加载器: " + className);
//        }
//
//        registerSPIForcefully(ILaunchHandlerService.class, impl);
//    }

    public static <T> void registerSPIForcefully(Class<T> spi, Class<?> impl) throws Exception {
        Field providersField = ServiceLoader.class.getDeclaredField("providers");
        providersField.setAccessible(true);

        ServiceLoader<T> loader = ServiceLoader.load(spi, ClassLoader.getSystemClassLoader());
        Map<String, T> map = (Map<String, T>) providersField.get(loader);

        Constructor<?> ctor = impl.getDeclaredConstructor();
        ctor.setAccessible(true);
        T instance = (T) ctor.newInstance();

        map.put(impl.getName(), instance);
    }

    public static byte[] readClassBytes(Path jarPath, String className) throws IOException {
        String entryName = className.replace('.', '/') + ".class";
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry(entryName);
            if (entry == null) {
                throw new FileNotFoundException("Class " + className + " not found in " + jarPath);
            }
            try (InputStream is = jarFile.getInputStream(entry)) {
                return readAllBytes(is);
            }
        }
    }


    public static void registerSpiCao(Map<String, List<String>> allSpi, ClassLoader loader) {
        for (Map.Entry<String, List<String>> entry : allSpi.entrySet()) {
            String spiInterface = entry.getKey();
            List<String> impls = entry.getValue();
            try {
                ServiceLoader<Object> objects = manuallyRegisterSPI(spiInterface, impls, loader);

            } catch (Exception e) {
                System.err.println("❌ 注册 SPI 失败: " + spiInterface + " -> " + impls);
                e.printStackTrace();
            }
        }
    }

//    public static Class<?> defineClassToSystem(String className, byte[] bytes) {
//        return UnsafeHelper.unsafe.defineClass(
//                className, bytes, 0, bytes.length,
//                ClassLoader.getSystemClassLoader(), null
//        );
//    }

    @SuppressWarnings("unchecked")
    public static <T> ServiceLoader<T> manuallyRegisterSPI(String spiInterfaceName, List<String> implClassNames, ClassLoader loader) {
        try {

            Class<T> spiInterface = (Class<T>) Class.forName(spiInterfaceName, false, loader);

            ServiceLoader<T> serviceLoader = ServiceLoader.load(spiInterface, loader);

            Field providersField = ServiceLoader.class.getDeclaredField("providers");
            providersField.setAccessible(true);
            Map<String, T> providers = (Map<String, T>) providersField.get(serviceLoader);

            for (String implClassName : implClassNames) {
                try {
                    Class<?> implClass = Class.forName(implClassName, false, loader);
                    T instance = (T) implClass.getDeclaredConstructor().newInstance();
                    providers.put(implClassName, instance);
                    System.out.println(" 注册 SPI [" + spiInterfaceName + "] -> " + implClassName);
                } catch (Exception e) {
                    throw new RuntimeException("手动注册 SPI 实现类失败: " + implClassName, e);
                }
            }

            return serviceLoader;

        } catch (Exception e) {
            throw new RuntimeException("手动注册 SPI 接口失败: " + spiInterfaceName, e);
        }
    }


//    public static void defineClass(Path jarPath, ClassLoader loader, String importantClass, ProtectionDomain pd) throws IOException {
//        try (JarFile jar = new JarFile(jarPath.toFile())) {
//            defineClass(jar, loader, importantClass, pd);
//        }
//    }
//
//    public static void defineClass(JarFile jar, ClassLoader loader, String importantClass, ProtectionDomain pd) throws IOException {
//        JarEntry entry = jar.getJarEntry(importantClass.replace('.', '/') + ".class");
//        if (entry != null) {
//            try (InputStream is = jar.getInputStream(entry)) {
//                byte[] bytes = readAllBytes(is);
//                defineClassSafely(importantClass, bytes, loader, pd, false);
//            }
//        }
//    }

    /**
     * Gently KISS every class in the JAR into the custom loader.
     * No violence, no delegation. Only passion.
     */
//    public static void kissAllFromJar(Path jarPath, ClassLoader loader, List<String> forceDefineFirst) throws Exception {
//
//        ProtectionDomain pd = new ProtectionDomain(new CodeSource(jarPath.toUri().toURL(), (Certificate[]) null), null);
//
//
//        try (JarFile jar = new JarFile(jarPath.toFile())) {
//            // 🔁 提前 define 重要类
//            for (String importantClass : forceDefineFirst) {
//                JarEntry entry = jar.getJarEntry(importantClass.replace('.', '/') + ".class");
//                if (entry != null) {
//                    try (InputStream is = jar.getInputStream(entry)) {
//                        byte[] bytes = readAllBytes(is);
//                        defineClassSafely(importantClass, bytes, loader, pd, false);
//                    }
//                }
//            }
//            Enumeration<JarEntry> entries = jar.entries();
//            while (entries.hasMoreElements()) {
//                JarEntry entry = entries.nextElement();
//                if (entry.getName().endsWith(".class")) {
//
//                    String className = entry.getName().replace('/', '.').replaceAll("\\.class$", "");
//                    // 🔒 如果类已经被加载，跳过
//                    try (InputStream is = jar.getInputStream(entry)) {
//                        byte[] bytes = readAllBytes(is);
//                        defineClassSafely(className, bytes, loader, pd, false);
//                    } catch (IOException e) {
//                        System.out.println("无法读取 " + entry.getName());
//                    }
//                }
//            }
//        }
//    }


//    public static Class<?> defineClassSafely(String className, byte[] bytecode, ClassLoader loader, ProtectionDomain pd, boolean verbose) {
//
//        Objects.requireNonNull(className, "Class name cannot be null");
//        Objects.requireNonNull(bytecode, "Bytecode cannot be null");
//        Objects.requireNonNull(loader, "ClassLoader cannot be null");
//        if (isLoadedByAny(className, loader)) {
//            if (verbose) {
//                System.out.println("⚠️ Class already loaded by system loader: " + className);
//            }
//            return null;
//        }
//        try {
//            // 尝试通过 loader 判断是否已经加载
//            try {
//                Class<?> already = Class.forName(className, false, loader);
//                if (verbose) {
//                    System.out.println("[defineClassSafely] Already loaded: " + className);
//                }
//                return already;
//            } catch (ClassNotFoundException ignored) {
//
//            }
//
//
//            Class<?> defined = UnsafeHelper.unsafe.defineClass(
//                    className, bytecode, 0, bytecode.length, loader, pd);
//            if (verbose) {
//                System.out.println("[defineClassSafely] Defined: " + className);
//            }
//            return defined;
//        } catch (LinkageError e) {
//            System.err.println("[FAIL] Linkage/Verify error: " + className + " - " + e.getMessage());
//        }
//
//
//        return null;
//    }
    public static boolean isLoadedByAny(String className, ClassLoader loader) {
        try {
            Class.forName(className, false, loader);
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            // 启动类加载器（BootLoader）
            ClassLoader bootLoader = null;
            Class.forName(className, false, bootLoader);
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }


    private static final Set<String> definedClasses = ConcurrentHashMap.newKeySet();


    private static byte[] loadJarBytes(Path jarFile, String className) {
        try (JarFile jar = new JarFile(jarFile.toFile())) {
            JarEntry entry = jar.getJarEntry(className);
            if (entry == null) {
                throw new RuntimeException("❌ 找不到类: " + className + " in " + jarFile);
            }
            try (InputStream is = jar.getInputStream(entry)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ 读取字节码失败: " + e.getMessage(), e);
        }
    }


    public static List<URL> loadAllLibraries(Path libRoot, Path excludeJar) throws IOException {
        List<URL> urls = new ArrayList<>();
        Files.walk(libRoot)
                .filter(p -> p.toString().endsWith(".jar"))
                .filter(p -> !p.toAbsolutePath().equals(excludeJar.toAbsolutePath()))
                .forEach(p -> {
                    try {
                        urls.add(p.toUri().toURL());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return urls;
    }


    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static <T> void printSPIImplementations(Class<T> serviceInterface, ClassLoader loader) {
        System.out.println("== SPI 检查开始: " + serviceInterface.getName() + " ==");
        try {
            ServiceLoader<T> loaderInstance = ServiceLoader.load(serviceInterface, loader);
            int count = 0;
            for (T impl : loaderInstance) {
                System.out.println("找到实现类: " + impl.getClass().getName()
                        + " 加载器: " + impl.getClass().getClassLoader());
                count++;
            }
            if (count == 0) {
                System.out.println("⚠️ 没有找到实现类！");
            }
        } catch (Throwable t) {
            System.out.println("❌ 加载 SPI 失败: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
        }
        System.out.println("== SPI 检查结束 ==\n");
    }


    private static List<String> forge() {
        return Arrays.asList(
                "net/minecraftforge/server/ServerMain",
                "net/minecraftforge/server/ServerMain$Runner",
                "net.minecraftforge.fml.loading.progress.EarlyProgressVisualization$Visualization",
                "net.minecraftforge.fml.loading.progress.EarlyProgressVisualization$NoVisualization",
                "net/minecraftforge/fml/loading/log4j/ForgeHighlight",
                "net/minecraftforge/fml/loading/FMLCommonLaunchHandler",
                "net/minecraftforge/fml/loading/FMLClientLaunchProvider",
                "net/minecraftforge/fml/loading/FMLServerLaunchProvider",
                "net/minecraftforge/fml/loading/RuntimeDistCleaner",
                "net/minecraftforge/common/asm/RuntimeEnumExtender",
                "net/minecraftforge/common/asm/ObjectHolderDefinalize",
                "net/minecraftforge/common/asm/CapabilityInjectDefinalize",
                "net/minecraftforge/fml/loading/ModDirTransformerDiscoverer",
                "net/minecraftforge/fml/loading/FMLPaths",
                "net/minecraftforge/fml/loading/FMLServiceProvider",
                "net/minecraftforge/fml/loading/TracingPrintStream"
        );
    }


    public static String[] collectArgs(String[] args) {
        Set<String> passedOptions = Arrays.stream(args).collect(Collectors.toSet());
        List<String> finalArgs = new ArrayList<>();
        for (String s : VersionInfo.LAUNCHER_ARGS) {
            if (s.startsWith("--launchTarget") && passedOptions.contains("--launchTarget")) continue;
            finalArgs.add(s);
        }
        finalArgs.addAll(Arrays.asList(args));

        return finalArgs.toArray(new String[0]);
    }


}
