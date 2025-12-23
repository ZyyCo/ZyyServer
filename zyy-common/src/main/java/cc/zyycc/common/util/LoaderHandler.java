package cc.zyycc.common.util;

import cc.zyycc.common.bridge.InstrumentationBridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

public class LoaderHandler {


    public static void addSystemClassLoader(List<Path> paths) {
        for (Path path : paths) {
            try {
                JarFile jarFile = new JarFile(path.toFile());
                InstrumentationBridge.getInst().appendToSystemClassLoaderSearch(jarFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static List<JarFile> pathToJarFile(List<Path> paths) {
        List<JarFile> jarFiles = new ArrayList<>();
        for (Path path : paths) {
            try {
                jarFiles.add(new JarFile(path.toFile()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jarFiles;
    }


    public static void addToPaths(ClassLoader loader, List<Path> paths) throws Exception {

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

        for (Path path : paths) {
            addUrlMethod.invoke(ucp, path.toUri().toURL());
        }
    }

    public static boolean isBukkitCaller() {
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            try {
                Class<?> cls = Class.forName(el.getClassName(), false, null);
                ClassLoader cl = cls.getClassLoader();

                // ① PluginClassLoader（插件）
                if (cl != null && cl.getClass().getName().contains("PluginClassLoader")) {
                    return true;
                }

                // ② CraftBukkit 本身（通常 AppClassLoader 但包路径是 org.bukkit.*）
                if (el.getClassName().startsWith("org.bukkit")) {
                    return true;
                }

            } catch (Throwable ignored) {}
        }
        return false;
    }




}
