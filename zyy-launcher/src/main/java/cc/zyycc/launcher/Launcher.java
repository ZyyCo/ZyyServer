package cc.zyycc.launcher;

import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.bridge.BridgeHolder;
import cc.zyycc.common.bridge.InstrumentationBridge;
import cc.zyycc.common.util.Version;
import cc.zyycc.forge.MainForge;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public class Launcher {


    public static final String WORKING_DIR = ".zyy";


    public static void main(String[] args) {

        if (!Version.checkJavaVersion()) {
            System.err.println("版本错误");
            System.exit(1);
        }
        try {

            // 准备服务端目录（配置文件）
            boolean canStart = ServerInitializer.prepareServerDirectory(new File(VersionInfo.WORKING_DIR));
            //创建缓存 目录
            Files.createDirectories(Paths.get(WORKING_DIR));

            BridgeHolder.setOptionParser(args);

            ClassLoader myLoader = InitLib.initLoader();

            InitLib.startForgeServer(args, myLoader, canStart);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


//
//    private static void addToPath(ClassLoader loader, Path path) throws Throwable {
//
//        Field ucpField;
//        try {
//            ucpField = loader.getClass().getDeclaredField("ucp");
//        } catch (NoSuchFieldException e) {
//            ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
//        }
//        long offset = Unsafe.objectFieldOffset(ucpField);
//        Object ucp = Unsafe.getObject(loader, offset);
//        Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
//        Unsafe.lookup().unreflect(method).invoke(ucp, path.toUri().toURL());
//    }

//    private static void openAttachModule() {
//        try {
//            Class<?> vmClass = Class.forName("com.sun.tools.attach.VirtualMachine");
//            Module jdkAttach = vmClass.getModule();
//            Module current = Launcher.class.getModule();
//            Method addOpens = Module.class.getDeclaredMethod("addOpens", String.class, Module.class);
//            addOpens.setAccessible(true);
//            addOpens.invoke(jdkAttach, "sun.tools.attach", current);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }

    private static Path loadToolsPath() {
        Path toolsPath = Paths.get("C:/Program Files/Java/jdk1.8.0_321/lib/tools.jar");
        if (!toolsPath.toFile().exists()) {
            try {
                Path javaHome = Paths.get(System.getProperty("java.home"));
                toolsPath = javaHome.resolve("lib").resolve("tools.jar");
                return toolsPath;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return toolsPath;
    }

    public static void loadAgent(ClassLoader loader, Path agentPath) {

        try {

            Class<?> jdkAttach = loader.loadClass("com.sun.tools.attach.VirtualMachine");

            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            // attach 到当前 JVM
            Object vm = jdkAttach.getMethod("attach", String.class).invoke(null, pid);

            jdkAttach.getMethod("loadAgent", String.class)
                    .invoke(vm, agentPath.toAbsolutePath().toString());
            // detach
            jdkAttach.getMethod("detach").invoke(vm);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ 未安装 JDK，缺少 VirtualMachine，请安装完整 JDK");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Agent 注入失败: " + e.getMessage());
            System.exit(1);
        }
    }

//    private static Class<?> loadClassUsingUnsafe(String className, ClassLoader classLoader) throws Exception {
//        // 获取类的字节码数据
//        byte[] classData = getClassData(className);
//        UnsafeHelper.unsafe.
//        // 通过 Unsafe.defineClass 手动加载类
//        return  UnsafeHelper.unsafe.defineClass(className, classData, 0, classData.length, classLoader, null);
//    }


}

