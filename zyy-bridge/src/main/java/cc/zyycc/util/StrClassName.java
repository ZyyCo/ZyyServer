package cc.zyycc.common.util;

import cc.zyycc.common.cache.StrCache;

public class StrClassName {

    public static final StrCache STR_CLASSES = new StrCache("str_classes");


    public static boolean hasStrClass() {
        return !STR_CLASSES.hasCache();
    }

    public static String getStrClass(String str) {
        return STR_CLASSES.getSuccessCache(str);
    }

    public static void writeStrClass(String className) {
        STR_CLASSES.writeFile(className.substring(className.lastIndexOf('/') + 1), className);
    }


}
