package cc.zyycc.util;

import java.util.function.Predicate;

public class NmsDetector {
    public static final Predicate<String> IS_BK_NMS_CLASS = clazz -> clazz.startsWith("net/minecraft/server/v1_");

    private static final String BUKKIT_NMS_PREFIX = "net/minecraft/server/v1_";
    public static boolean isBkNms(String clazz) {
        return clazz.startsWith(BUKKIT_NMS_PREFIX);
    }


}
