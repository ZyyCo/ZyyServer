package cc.zyycc.common.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SafeMethodBridge {
    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            Class.forName("cc.zyycc.remap.method.SafeMethodHelper", true, LoaderManager.getClassLoader(MyLoader.AGENT))
                    .getMethod("getMethod", Class.class, String.class, Class[].class)
                    .invoke(null, clazz, name, params);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();

        }
        return null;
    }
}
