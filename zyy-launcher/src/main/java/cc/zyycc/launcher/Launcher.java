package cc.zyycc.launcher;

import cc.zyycc.common.VersionInfo;
import cc.zyycc.core.util.LoaderHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Launcher {
    public static void main(String[] args) throws Exception {

        // 准备服务端目录（配置文件）
        ServerInitializer.prepareServerDirectory(new File(VersionInfo.FORGE_LOCAL_PATH));


        //创建工作目录
        Files.createDirectories(Paths.get(VersionInfo.WORKING_DIR));
        //forge
        String forgeInternalPathInJar = "/" + VersionInfo.INTERNALPATH + "/zyy.jar";
        Path cacheForge = Paths.get(VersionInfo.WORKING_DIR, "zyyaruzi.jar");
        extractInternalJar(forgeInternalPathInJar, cacheForge);
        //core
        String coreInternalPathInJar = "/" + VersionInfo.INTERNALPATH + "/zyy-core.jar";
        Path cacheCore = Paths.get(VersionInfo.WORKING_DIR, "nashicore.jar");
        extractInternalJar(coreInternalPathInJar, cacheCore);
        //common
        String commonInternalPathInJar = "/" + VersionInfo.INTERNALPATH + "/zyy-common.jar";
        Path cacheCommon = Paths.get(VersionInfo.WORKING_DIR, "kissCommon.jar");
        extractInternalJar(commonInternalPathInJar, cacheCommon);

        premountCommon(cacheCommon);
//        addToPath(ClassLoader.getSystemClassLoader(), cacheCore);
//        addToPath(ClassLoader.getSystemClassLoader(), cacheForge);
//        Class<?> aClass = Class.forName("cc.zyycc.forge.MainForge");
//        aClass.getMethod("startForgeServer", String[].class).invoke(aClass, (Object) args);

        URL[] allUrls = new URL[]{
                cacheCore.toUri().toURL(),
                cacheForge.toUri().toURL(),
                Launcher.class.getProtectionDomain().getCodeSource().getLocation()
        };



        try (URLClassLoader loader = new URLClassLoader(allUrls, Launcher.class.getClassLoader())) {

            Class<?> cl = loader.loadClass("cc.zyycc.forge.MainForge");

            loadAgent(new URL[]{cacheCore.toUri().toURL(), new File("C:/Program Files/Java/jdk1.8.0_321/lib/tools.jar").toURI().toURL()});

            cl.getMethod("startForgeServer", String[].class)
                    .invoke(null, (Object) args);

        }

    }

    public static void premountCommon(Path kissCommonJar) throws Exception {
        URL commonUrl = kissCommonJar.toUri().toURL();
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

        if (!(systemLoader instanceof URLClassLoader)) {
            return;
        }

        Field ucpField;
        try {
            ucpField = URLClassLoader.class.getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            ucpField = systemLoader.getClass().getSuperclass().getDeclaredField("ucp");
        }
        ucpField.setAccessible(true);
        Object ucp = ucpField.get(systemLoader);

        Method addURL = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        addURL.invoke(ucp, commonUrl);

    }


    public static Path extractInternalJar(String internalPathInJar, Path cacheDir) throws IOException {
        try (InputStream is = Launcher.class.getResourceAsStream(internalPathInJar)) {
            if (is == null) {
                throw new FileNotFoundException("没找到内嵌核心资源: " + internalPathInJar);
            }
            //Path tempJar = Files.createTempFile("core-", ".jar");
            Files.copy(is, cacheDir, StandardCopyOption.REPLACE_EXISTING);
//            tempJar.toFile().deleteOnExit();
            return cacheDir;
        }
    }


    public static void loadAgent(URL[] url) {

        try (URLClassLoader loader = new URLClassLoader(url)) {

            Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //        VirtualMachine vm = VirtualMachine.attach(pid);
            //        vm.loadAgent("your-agent.jar", ""); // 可选传入参数
            // attach 到当前 JVM
            Object vm = vmClass.getMethod("attach", String.class).invoke(null, pid);
            // 提取 agent 到工作目录
            String agentInternalPathInJar = "/" + VersionInfo.INTERNALPATH + "/zyy-agent.jar";
            Path cacheAgent = Paths.get(VersionInfo.WORKING_DIR, "agent.jar");
            extractInternalJar(agentInternalPathInJar, cacheAgent);

            // 调用 loadAgent 方法
            // String options = "loaderClass=cc.zyycc.core.bridge.PluginLoaderBridge;loaderField=INSTANCE";
            vmClass.getMethod("loadAgent", String.class)
                    .invoke(vm, cacheAgent.toAbsolutePath().toString());
            // detach
            vmClass.getMethod("detach").invoke(vm);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ 未安装 JDK，缺少 VirtualMachine，请安装完整 JDK");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Agent 注入失败: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void addToPath(ClassLoader loader, Path path) throws Exception {

        Field ucpField;
        try {
            ucpField = loader.getClass().getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
        }
        ucpField.setAccessible(true);
        Object ucp = ucpField.get(loader);
        Method addUrlMethod = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);

        addUrlMethod.invoke(ucp, path.toUri().toURL());

    }

}

