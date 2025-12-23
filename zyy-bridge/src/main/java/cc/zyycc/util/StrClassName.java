package cc.zyycc.util;


import cc.zyycc.remap.cache.StrCache;

public class StrClassName {

    public static final StrCache STR_CLASSES = new StrCache("str_classes");

    static {
        if (!ConfusionStr.hasStrFile()) {
            ConfusionStr.writeStrFile();
        }
        ConfusionStr.load();
    }

    public static void load() {
        STR_CLASSES.load(STR_CLASSES.getCacheFile(), (rule) -> STR_CLASSES.cacheMap.put(rule.left, rule.right));
    }

    public static boolean hasStrClassFile() {
        return STR_CLASSES.hasFile();
    }

    public static String getStrClassName(String str) {
        String successCache = STR_CLASSES.getSuccessCache(str);
        if (successCache != null) {
            return successCache;
        }
        throw new RuntimeException("未找到字符串对应的类名：" + str);
    }

    public static Clazz getStrClass(String str) {
        String successCache = STR_CLASSES.getSuccessCache(str);
        if (successCache != null) {
            return new Clazz(successCache);
        }
        throw new RuntimeException("未找到字符串对应的类名：" + str);
    }

    public static void writeCacheToFile() {
        STR_CLASSES.writeCacheToFile(STR_CLASSES.getCacheFile(), STR_CLASSES.cacheMap);
    }

    public static void setStrClass(String className) {
        STR_CLASSES.cacheMap.put(className.substring(className.lastIndexOf('/') + 1), className);
    }


    public static void clear() {
        STR_CLASSES.cacheMap.clear();
        ConfusionStr.STR_FIELDS.cacheMap.clear();
        ConfusionStr.STR_METHODS.cacheMap.clear();
    }

    public static class Clazz {

        public String className;

        public Clazz(String className) {
            this.className = className;
        }


        public String getConfusionMethod(String methodName) {
            return ConfusionStr.getStrMethod(className, methodName);
        }

        public String getConfusionField(String fieldName) {
            return ConfusionStr.getStrField(className, fieldName);
        }

        public String getClassName() {
            return className;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Clazz) {
                return className.equals(((Clazz) obj).className);
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return className;
        }
    }

}
