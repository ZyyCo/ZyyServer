package cc.zyycc.common.bridge;

import cc.zyycc.common.cache.CacheMapperData;
import cc.zyycc.common.cache.StrMappingCache;
import cc.zyycc.common.loader.LoaderManager;
import cc.zyycc.common.loader.MyLoader;
import cc.zyycc.common.util.CacheUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class SafeFiledBridge {

    public static final StrMappingCache cache =
            new StrMappingCache("success_reflection_field_mapping", "fail_reflection_field_mapping");

    private static final ConcurrentHashMap<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        String key = clazz.getName() + "#" + fieldName;
        Field cached = FIELD_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        if (clazz.getName().startsWith("org.bukkit.craftbukkit.v") && !clazz.getName().startsWith("net.minecraft")) {
            try {
                Field field = clazz.getField(fieldName);
                FIELD_CACHE.put(key, field);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        try {
            Field cacheField = getCacheField(clazz, fieldName, false);
            FIELD_CACHE.put(key, cacheField);
            return cacheField;
        } catch (NoSuchFieldException ignored) {
        }

        Field field = clazz.getField(fieldName);
        FIELD_CACHE.put(key, field);
        return field;
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException{
        String key = clazz.getName() + "#" + fieldName;
        Field cached = FIELD_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        if (clazz.getName().startsWith("org.bukkit.craftbukkit.v") && !clazz.getName().startsWith("net.minecraft")) {
            try {
                Field declaredField = clazz.getDeclaredField(fieldName);
                FIELD_CACHE.put(key, declaredField);
                return declaredField;
            } catch (NoSuchFieldException ignored) {
            }
        }
        try {
            Field cacheField = getCacheField(clazz, fieldName, true);
            FIELD_CACHE.put(key, cacheField);
            return cacheField;
        } catch (NoSuchFieldException ignored) {
        }

        Field declaredField = clazz.getDeclaredField(fieldName);
        FIELD_CACHE.put(key, declaredField);
        return declaredField;
    }

    public static Field getCacheField(Class<?> clazz, String fieldName, boolean isDeclared) throws NoSuchFieldException {
        CacheMapperData data = CacheUtil.parseFieldCache(cache, clazz, fieldName);
        if (data != null) {
            if (data.isError()) {
                return clazz.getField(fieldName);
            }
            try {
                Class<?> aClass = Class.forName(data.getClassName().replace("/", "."),
                        true, clazz.getClassLoader());
                return aClass.getDeclaredField(data.getName2());
            } catch (ClassNotFoundException e) {
                return clazz.getField(fieldName);
            }
        }

        try {
            Class<?> aClass = Class.forName("cc.zyycc.remap.field.SafeField", true, LoaderManager.getClassLoader(MyLoader.AGENT));
            if (isDeclared) {
                return (Field) aClass.getMethod("getDeclaredField", Class.class, String.class).invoke(null, clazz, fieldName);
            } else {
                return (Field) aClass.getMethod("getField", Class.class, String.class).invoke(null, clazz, fieldName);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException ignored) {
        }
        throw new NoSuchFieldException();
    }

}
