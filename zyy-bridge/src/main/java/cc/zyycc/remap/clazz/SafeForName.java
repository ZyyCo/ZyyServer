package cc.zyycc.remap;


import java.lang.reflect.Method;

public class SafeForName {
    public static Class<?> forName(String name, String pluginName) throws ClassNotFoundException {
        return forName(name, true, pluginName);
    }

    public static Class<?> forName(String name, boolean init, String pluginName)
            throws ClassNotFoundException {
        ClassLoader classLoader = LoaderRegistry.get(pluginName);
        if (classLoader != null) {
            LoaderRegistry.pluginLoaders.put(pluginName, classLoader);
        }

        try {
            String s = MappingHelper.getMappingClass(name.replace(".", "/"));
            return classLoader.loadClass(s);
        } catch (Throwable e) {
            try {
                // 兜底调用 native forName0，绕过 constraint 检查
                Method m = Class.class.getDeclaredMethod("forName0",
                        String.class, boolean.class, ClassLoader.class);
                m.setAccessible(true);
                return (Class<?>) m.invoke(null, name, init, classLoader);
            } catch (Throwable ex) {
                throw new ClassNotFoundException(name, ex);
            }
        }
    }


}
