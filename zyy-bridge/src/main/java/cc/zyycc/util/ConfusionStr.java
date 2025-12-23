package cc.zyycc.util;

import cc.zyycc.remap.cache.StrCache;

import java.io.IOException;
import java.io.InputStream;


public class ConfusionStr {
    public static final StrCache STR_FIELDS = new StrCache("str_confusion_field");
    public static final StrCache STR_METHODS = new StrCache("str_confusion_method");

    public static boolean hasStrFile() {
        return STR_FIELDS.hasFile() && STR_METHODS.hasFile();
    }


    public static String getStrField(String className, String field) {
        String successCache = STR_FIELDS.getSuccessCache(className + '#' + field);
        if (successCache != null) {
            return successCache;
        }
        throw new RuntimeException("未找到字符串对应的字段名：" + className + "/" + field);
    }

    public static String getStrMethod(String className, String str) {
        String successCache = STR_METHODS.getSuccessCache(className + '#' + str);
        if (successCache != null) {
            return successCache;
        }
        throw new RuntimeException("未找到字符串对应的方法名：" + className + "/" + str);
    }

    public static void writeStrFile() {
        try (InputStream in = ConfusionStr.class.getClassLoader().getResourceAsStream("mappings/confusion_fField.txt")) {
            STR_FIELDS.addFile(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream in = ConfusionStr.class.getClassLoader().getResourceAsStream("mappings/confusion_fMethod.txt")) {
            STR_METHODS.addFile(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void load() {
        STR_FIELDS.load(STR_FIELDS.getCacheFile(), (rule) -> STR_FIELDS.cacheMap.put(rule.left, rule.right));
        STR_METHODS.load(STR_METHODS.getCacheFile(), (rule) -> STR_METHODS.cacheMap.put(rule.left, rule.right));
    }
}
