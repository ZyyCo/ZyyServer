package cc.zyycc.forge;

import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.bridge.BridgeHolder;
import cc.zyycc.core.util.ClassPathUtils;
import cc.zyycc.core.util.LoaderHandler;
import cc.zyycc.core.util.ResourceUtils;
import cc.zyycc.installer.InstallForge;
import cc.zyycc.zyyaruzi.UnsafeHelper;
import cpw.mods.modlauncher.api.*;


import java.io.*;
import java.lang.reflect.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.*;
import java.util.stream.Collectors;


public class MainForge {
    public static List<Path> classPath = new ArrayList<>();
    public static List<Path> systemClassPath = new ArrayList<>();

    public static void startForgeServer(String[] args) throws Exception {

        String minecraftVersion = VersionInfo.MINECRAFT_VERSION;
        String forgeVersion = VersionInfo.FORGE_VERSION;
        String forgeLocalPath = VersionInfo.FORGE_LOCAL_PATH;
        // Path argsPath = ForgeResolver.getForgeFormArgsPath(minecraftVersion, forgeVersion, forgeLocalPath);

        String[] parts = minecraftVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);


        Path forgePath = Paths.get(forgeLocalPath, "forge-" + VersionInfo.FORGE_FULL_VERSION + ".jar");
        //安装
        String[] libs = installForge(forgePath);

        punchForge1165_hardcoded(args, libs, forgePath);

    }

    public static String[] installForge(Path forgeJar) throws IOException, InterruptedException {
        return InstallForge.checkOrInstall(VersionInfo.INTERNALPATH_INSTALL_DIR,
                VersionInfo.INSTALLER_FILE, VersionInfo.FORGE_LOCAL_PATH, forgeJar,
                () -> ClassPathUtils.loadClassPath(forgeJar));
    }


    public static Path resolveLocalPath(String url) {
        String relative = url.replaceFirst("^https?://[^/]+/", "");
        return Paths.get("libs").resolve(relative.replace("/", File.separator));
    }


    public static void punchForge1165_hardcoded(String[] args, String[] libraries, Path forgeJar) throws Exception {

        systemClassPath.add(new File("libraries/org/apache/logging/log4j/log4j-api/2.15.0/log4j-api-2.15.0.jar").toPath());
        systemClassPath.add(new File("libraries/org/apache/logging/log4j/log4j-core/2.15.0/log4j-core-2.15.0.jar").toPath());
        systemClassPath.add(new File("libraries/org/apache/logging/log4j/log4j-slf4j18-impl/2.15.0/log4j-slf4j18-impl-2.15.0.jar").toPath());
        classPath.add(new File("libraries/com/google/guava/guava/25.1-jre/guava-25.1-jre.jar").toPath());

        classPath.add(forgeJar);
        classPath.add(Paths.get(VersionInfo.WORKING_DIR, "zyyaruzi.jar"));

        //   classPath.add(new File(VersionInfo.FORGE_LOCAL_PATH, "libraries/net/minecraftforge/forge/1.16.5-36.2.34/forge-1.16.5-36.2.34-server.jar").toPath());

        //systemClassPath.add(new File("libraries/com/google/guava/guava/20.0/guava-20.0.jar").toPath());
        Path modLauncherPath = null;

//        String[] mustAdd = {
////                "modlauncher", "modlauncher-api", "modlauncher-serviceapi","net.minecraftforge", "grossjava9hacks",""
////                "log4j",  "jopt-simple", "mixin", "forgespi", "guava", "modlauncher"
//                "asm",
//        };


        for (String lib : libraries) {
            if (lib == null) continue;
            Path libPath = Paths.get(lib);
            if (lib.contains("modlauncher-")) {
                modLauncherPath = libPath;
//                classPath.add(libPath);
//                continue;
            }

            systemClassPath.add(libPath);
        }

        systemClassPath.add(new File(VersionInfo.FORGE_LOCAL_PATH, "minecraft_server.1.16.5.jar").toPath());


        ClassLoader appLoader = ClassLoader.getSystemClassLoader();

        LoaderHandler.addToPaths(appLoader, systemClassPath);


        URL[] urls = classPath.stream()
                .map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(URL[]::new);

        URLClassLoader myLoader = new URLClassLoader(urls, appLoader);


        BridgeHolder.INSTANCE.setClassLoader(myLoader);







        //bk
//        Path cacheBK = Paths.get(VersionInfo.WORKING_DIR, "bkao.jar");
//        URLClassLoader bkLoader = new URLClassLoader(new URL[]{cacheBK.toUri().toURL()}, myLoader);
//        Class.forName("cc.zyycc.zyyserver.A", false, bkLoader).getMethod("main", String[].class).invoke(null, (Object) args);


        //kissAllFromJar(forgeJar, myLoader, forge());


        //definePackageFromJarManifest(myLoader, forgeJar);


        //de

        createDefaultConfig();

        Class<?> sysLauncherClass = Class.forName("cpw.mods.modlauncher.Launcher");


        Method runLauncher = sysLauncherClass.getMethod("main", String[].class);



        runLauncher.invoke(null, (Object) collectArgs(args));


    }

    private static void createDefaultConfig() {
        Path path = Paths.get(VersionInfo.FORGE_LOCAL_PATH, "config");

        ResourceUtils.createFileIfAbsent(path.resolve("fml.toml"),
                "# Enable forge global version checking\n" +
                        "versionCheck = true\n" +
                        "# does the splashscreen run\n" +
                        "splashscreen = true\n" +
                        "defaultConfigPath = \"defaultconfigs\"\n" +
                        "# max threads for parallel loading : -1 uses Runtime#availableProcessors\n" +
                        "maxThreads = -1\n" +
                        "\n");

        ResourceUtils.createFileIfAbsent(path.resolve("forge-common.toml"),
                "\n" +
                        "#General configuration settings\n" +
                        "[general]\n" +
                        "\t#Defines a default world type to use. The vanilla default world type is represented by 'default'.\n" +
                        "\t#The modded world types are registry names which should include the registry namespace, such as 'examplemod:example_world_type'.\n" +
                        "\tdefaultWorldType = \"default\"\n" +
                        "\n");


    }



    public static void definePackageFromJarManifest(ClassLoader loader, Path forgeJar) {
        try {
            Attributes attrs = ClassPathUtils.readManifestAttributes(forgeJar);
            definePackageManually(loader, attrs);
        } catch (Exception e) {
            System.err.println("[-] definePackage 失败: " + e);
            e.printStackTrace();
        }
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



    public static void registerLaunchHandlerToSystem(Path forgeJar) throws Exception {
        String className = "net.minecraftforge.fml.loading.FMLServerLaunchProvider";

        ClassLoader sysLoader = ClassLoader.getSystemClassLoader();
        Class<?> impl;

        try {
            // ✅ 优先尝试加载，避免重复 define
            impl = Class.forName(className, false, sysLoader);
            System.out.println("⚠ 已加载类，跳过 define: " + className);
        } catch (ClassNotFoundException e) {
            byte[] bytes = readClassBytes(forgeJar, className);
            impl = defineClassToSystem(className, bytes);
            System.out.println("✅ define 到系统类加载器: " + className);
        }

        registerSPIForcefully(ILaunchHandlerService.class, impl);
    }

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

    public static Class<?> defineClassToSystem(String className, byte[] bytes) {
        return UnsafeHelper.unsafe.defineClass(
                className, bytes, 0, bytes.length,
                ClassLoader.getSystemClassLoader(), null
        );
    }

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



    public static void defineClass(Path jarPath, ClassLoader loader, String importantClass, ProtectionDomain pd) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            defineClass(jar, loader, importantClass, pd);
        }
    }

    public static void defineClass(JarFile jar, ClassLoader loader, String importantClass, ProtectionDomain pd) throws IOException {
        JarEntry entry = jar.getJarEntry(importantClass.replace('.', '/') + ".class");
        if (entry != null) {
            try (InputStream is = jar.getInputStream(entry)) {
                byte[] bytes = readAllBytes(is);
                defineClassSafely(importantClass, bytes, loader, pd, false);
            }
        }
    }

    /**
     * Gently KISS every class in the JAR into the custom loader.
     * No violence, no delegation. Only passion.
     */
    public static void kissAllFromJar(Path jarPath, ClassLoader loader, List<String> forceDefineFirst) throws Exception {

        ProtectionDomain pd = new ProtectionDomain(new CodeSource(jarPath.toUri().toURL(), (Certificate[]) null), null);


        try (JarFile jar = new JarFile(jarPath.toFile())) {
            // 🔁 提前 define 重要类
            for (String importantClass : forceDefineFirst) {
                JarEntry entry = jar.getJarEntry(importantClass.replace('.', '/') + ".class");
                if (entry != null) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        byte[] bytes = readAllBytes(is);
                        defineClassSafely(importantClass, bytes, loader, pd, false);
                    }
                }
            }
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {

                    String className = entry.getName().replace('/', '.').replaceAll("\\.class$", "");
                    // 🔒 如果类已经被加载，跳过
                    try (InputStream is = jar.getInputStream(entry)) {
                        byte[] bytes = readAllBytes(is);
                        defineClassSafely(className, bytes, loader, pd, false);
                    } catch (IOException e) {
                        System.out.println("无法读取 " + entry.getName());
                    }
                }
            }
        }
    }


    public static Class<?> defineClassSafely(String className, byte[] bytecode, ClassLoader loader, ProtectionDomain pd, boolean verbose) {

        Objects.requireNonNull(className, "Class name cannot be null");
        Objects.requireNonNull(bytecode, "Bytecode cannot be null");
        Objects.requireNonNull(loader, "ClassLoader cannot be null");
        if (isLoadedByAny(className, loader)) {
            if (verbose) {
                System.out.println("⚠️ Class already loaded by system loader: " + className);
            }
            return null;
        }
        try {
            // 尝试通过 loader 判断是否已经加载
            try {
                Class<?> already = Class.forName(className, false, loader);
                if (verbose) {
                    System.out.println("[defineClassSafely] Already loaded: " + className);
                }
                return already;
            } catch (ClassNotFoundException ignored) {

            }

            // defineClass 调用
            Class<?> defined = UnsafeHelper.unsafe.defineClass(
                    className, bytecode, 0, bytecode.length, loader, pd);
            if (verbose) {
                System.out.println("[defineClassSafely] Defined: " + className);
            }
            return defined;
        } catch (LinkageError e) {
            System.err.println("[FAIL] Linkage/Verify error: " + className + " - " + e.getMessage());
        }


        return null;
    }

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

    public static Class<?> defineClassFromJar(
            URLClassLoader loader, Path jarPath, String className, ProtectionDomain pd
    ) throws Exception {
        if (definedClasses.contains(className)) {
            return loader.loadClass(className); // 已定义则直接返回
        }

        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
            if (entry == null) throw new RuntimeException("❌ 找不到类: " + className + " in " + jarPath);

            try (InputStream is = jar.getInputStream(entry)) {
                byte[] bytes = readAllBytes(is);
                // 👉 此处你可以插入 ASM patch logic（可扩展）
                Class<?> clazz = UnsafeHelper.unsafe.defineClass(null, bytes, 0, bytes.length, loader, pd);
                definedClasses.add(className);
                return clazz;
            }
        }
    }


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
