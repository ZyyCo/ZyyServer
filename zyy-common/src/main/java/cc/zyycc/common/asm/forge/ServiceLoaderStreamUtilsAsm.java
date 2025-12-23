package cc.zyycc.common.asm.forge;

import cc.zyycc.common.loader.MyLoader;

public class ServiceLoaderStreamUtilsAsm {

    public static ClassLoader errorHandlingServiceLoaderASM(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return MyLoader.ZYY.classLoader();
        }else
        if (classLoader.getClass().getName().equals("cpw.mods.modlauncher.TransformationServicesHandler$TransformerClassLoader")) {
            return MyLoader.ZYY.classLoader();
        }
        return classLoader;
    }
}
