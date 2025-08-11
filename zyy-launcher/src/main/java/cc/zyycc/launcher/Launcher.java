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

        Path cacheZyy = extractInternalJar("zyy.jar", "zyyaruzi.jar", null);
        //core
        Path cacheCore = extractInternalJar("zyy-core.jar", "nashicore.jar", null);
        //common
        extractInternalJar("zyy-common.jar", "kissCommon.jar", ClassLoader.getSystemClassLoader());

        //agent
        Path cacheAgent = extractInternalJar("zyy-agent.jar", "agent.jar", null);



        Path toolsPath = Paths.get("C:/Program Files/Java/jdk1.8.0_321/lib/tools.jar");
        loadAgent(new URLClassLoader(new URL[]{toolsPath.toUri().toURL()}), cacheAgent);
//        addToPath(ClassLoader.getSystemClassLoader(), cacheCore);
//        addToPath(ClassLoader.getSystemClassLoader(), cacheZyy);
//        addToPath(ClassLoader.getSystemClassLoader(), cacheBK);
//        Class<?> aClass = Class.forName("cc.zyycc.forge.MainForge");
//
//        aClass.getMethod("startForgeServer", String[].class).invoke(aClass, (Object) args);


        URL[] allUrls = new URL[]{
                cacheCore.toUri().toURL(),
                cacheZyy.toUri().toURL(),
                // Launcher.class.getProtectionDomain().getCodeSource().getLocation()
        };


        try (URLClassLoader loader = new URLClassLoader(allUrls, Launcher.class.getClassLoader())) {

            Class<?> cl = loader.loadClass("cc.zyycc.forge.MainForge");

            cl.getMethod("startForgeServer", String[].class)
                    .invoke(null, (Object) args);

        }

    }


    public static Path extractInternalJar(String internalPathInJar, String cacheDir, ClassLoader premountClassLoader) throws IOException {

        String pathInJar = "/" + VersionInfo.INTERNALPATH + "/" + internalPathInJar;
        try (InputStream is = Launcher.class.getResourceAsStream(pathInJar)) {
            if (is == null) {
                throw new FileNotFoundException("没找到内嵌核心资源: " + pathInJar);
            }
            Path path = Paths.get(VersionInfo.WORKING_DIR, cacheDir);
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            if (premountClassLoader != null) {
                addToPath(premountClassLoader, path);
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public static void loadAgent(ClassLoader loader, Path cacheAgent) {

        try {
            Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

            String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

            // attach 到当前 JVM
            Object vm = vmClass.getMethod("attach", String.class).invoke(null, pid);


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

}

