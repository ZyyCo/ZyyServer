package cc.zyycc.common.bridge;

import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.cache.StrMappingCache;
import cc.zyycc.common.loader.LoaderManager;
import cc.zyycc.common.loader.MyLoader;

import java.lang.reflect.InvocationTargetException;

public class SafeClassForNameBridge {


    public static Class<?> forName(String name, boolean init, ClassLoader classLoader) throws ClassNotFoundException, NoClassDefFoundError {
        if (name.startsWith("net.minecraft.server.v1")) {
            try {
                return (Class<?>) Class.forName("cc.zyycc.remap.clazz.SafeForName", false, LoaderManager.getClassLoader(MyLoader.AGENT))
                        .getMethod("forName", String.class, ClassLoader.class).invoke(null, name, classLoader);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException) cause;
                } else if (cause instanceof NoClassDefFoundError) {
                    throw (NoClassDefFoundError) cause;
                }
            }

        } else {
            return Class.forName(name, init, classLoader);
        }

        return null;
    }

    public static Class<?> forName(String name, String pluginName) throws ClassNotFoundException, NoClassDefFoundError {
        return forName(name, true, LoaderManager.getClassLoader(pluginName));
    }

    public static Class<?> loadClass(ClassLoader loader, String name) throws ClassNotFoundException, NoClassDefFoundError {
        String prefix = "net.minecraft.server." + VersionInfo.BUKKIT_VERSION + ".";
        if (name.startsWith(prefix)) {
            String suffix = name.substring(prefix.length());
            if (suffix.contains(".")) {
                String[] split = suffix.split("\\.");
                suffix = split[split.length - 1];
                name = prefix + suffix;
            }

        }

        return forName(name, true, loader);
    }

}