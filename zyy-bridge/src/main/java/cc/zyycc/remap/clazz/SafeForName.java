package cc.zyycc.remap.clazz;


import cc.zyycc.remap.MappingHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SafeForName {
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return forName(name, true, classLoader);
    }


    public static Class<?> forName(String name, boolean init, ClassLoader classLoader) throws ClassNotFoundException {
        String s = MappingHelper.getMappingClass(name.replace(".", "/"));
        return classLoader.loadClass(s.replace("/", "."));

        //            // 兜底调用 native forName0，绕过 constraint 检查
//            Method m = Class.class.getDeclaredMethod("forName0",
//                    String.class, boolean.class, ClassLoader.class);
//            m.setAccessible(true);
//            return (Class<?>) m.invoke(null, name, init, classLoader);
    }


}
