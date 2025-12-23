package cc.zyycc.remap.filed;

import java.lang.reflect.Field;

public class SafeFiled {
    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException i) {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        }
        return null;
    }
}
