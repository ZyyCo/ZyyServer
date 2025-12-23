package cc.zyycc.util;

import cc.zyycc.remap.cache.StrCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StrConfusionMethodField {
    public static final StrCache STR_FIELDS = new StrCache("str_confusion_method");
    public static final StrCache STR_METHODS = new StrCache("str_confusion_field");

    public static boolean hasStrFile() {
        return STR_FIELDS.hasFile() && STR_METHODS.hasFile();
    }


    public static String getStrField(String str) {
        String successCache = STR_FIELDS.getSuccessCache(str);
        if (successCache != null) {
            return successCache;
        }
        throw new RuntimeException("未找到字符串对应的类名：" + str);
    }
    public static String getStrMethod(String str) {
        String successCache = STR_METHODS.getSuccessCache(str);
        if (successCache != null) {
            return successCache;
        }
        throw new RuntimeException("未找到字符串对应的类名：" + str);
    }

    public static void writeStrFile() {
        try (InputStream in = StrConfusionMethodField.class.getClassLoader().getResourceAsStream("mappings/confusion_fField.txt")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    STR_FIELDS.addFile(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream in = StrConfusionMethodField.class.getClassLoader().getResourceAsStream("mappings/confusion_fMethod.txt")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    STR_FIELDS.addFile(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
