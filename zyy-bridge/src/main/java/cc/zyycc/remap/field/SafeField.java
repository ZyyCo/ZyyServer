package cc.zyycc.remap.field;

import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.MappingHelper;
import cc.zyycc.remap.cache.*;

import java.lang.reflect.Field;
import java.util.function.Function;

public class SafeField {

    public static MappingCache<BaseEntry> cache = MappingCacheManager.FIELD_REFLECTION;

    public static Field getField(Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName, field -> {
            try {
                return clazz.getField(field);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName, field -> {
            try {
                return clazz.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static Field getField(Class<?> clazz, String fieldName, Function<String, Field> callback) {
        BaseEntry searchEntry = new BaseEntry(clazz.getName(), fieldName);
        String className = clazz.getName().replace(".", "/");
        if (!className.startsWith("net/minecraft/server/v1") && !className.startsWith("org/bukkit/craftbukkit/v")) {
            className = MappingHelper.getConvertedClass(className).orElse(className);
            if (className.startsWith("net/minecraft")) {
                String remapField = MappingHelper.remapField(className, fieldName);
                if (remapField != null) {
                    return callback.apply(remapField);
                }
                BaseEntry mappingEntry = MappingHelper.searchFieldInSuperClasses(searchEntry.getClassName(), fieldName, clazz.getClassLoader());
                if (mappingEntry != null) {
                    cache.addSuccessCache(searchEntry, mappingEntry);
                    return callback.apply(mappingEntry.getName2());
                }
            }
        }
        if (className.startsWith("org/bukkit/craftbukkit/v")) {
            BaseEntry mappingEntry = MappingHelper.searchFieldInSuperClasses(className, fieldName, clazz.getClassLoader());
            if (mappingEntry != null) {
                cache.addSuccessCache(searchEntry, mappingEntry);
                return mappingEntry.anewExecuteField(clazz.getClassLoader());
            }
        }
        return callback.apply(fieldName);
    }

}
