package cc.zyycc.common.loader;

public class SafeClassForNameBridge {

    public static Class<?> forName(String name, String pluginName) throws ClassNotFoundException {
        ClassLoader classLoader = LoaderManager.getClassLoader(pluginName);
        if (name.startsWith("net.minecraft.server.v1_")) {
            try {
                Class.forName("cc.zyycc.remap.clazz.SafeForName", false, LoaderManager.getClassLoader(MyLoader.AGENT))
                        .getMethod("forName", String.class, ClassLoader.class).invoke(null, name, classLoader);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            return Class.forName(name, true, classLoader);
        }


//        } else {
//            try {
//                return Class.forName(name, true, Class.forName(pluginClassLoader).getClassLoader());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }


        return null;
    }
}