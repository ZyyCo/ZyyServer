package cc.zyycc.common.bridge;

import cc.zyycc.common.cache.CacheMapperData;
import cc.zyycc.common.cache.StrMappingCache;
import cc.zyycc.common.loader.LoaderManager;
import cc.zyycc.common.loader.MyLoader;
import cc.zyycc.common.util.CacheUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SafeMethodBridge {

    public static final StrMappingCache cache =
            new StrMappingCache("success_reflection_method_mapping", "error_reflection_method_mapping");
    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> METHOD_FAIL_CACHE = ConcurrentHashMap.newKeySet();

    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
        String owner = clazz.getName().replace('.', '/');
        String bkDesc = Type.getMethodDescriptor(Type.VOID_TYPE, CacheUtil.paramsToTypes(params));
        String key = owner + "#" + name + "#" + bkDesc;
        Method cached = METHOD_CACHE.get(key);
        if (cached != null) return cached;
        if (METHOD_FAIL_CACHE.contains(key)) return null;
        if ((clazz.getName().startsWith("org.bukkit.craftbukkit.v") && !clazz.getName().startsWith("net.minecraft")) || clazz.getName().startsWith("java.lang")) {
            return getMethod(clazz, name, key, "插件类", false, params);
        }
        CacheMapperData data = CacheUtil.parseMethodCache(cache, clazz, name, bkDesc);
        if (data != null) {
            if (data.isError()) {
                METHOD_FAIL_CACHE.add(key);
                return null;
            }
            try {
                Class<?> target = Class.forName(data.getClassName().replace("/", "."), true, clazz.getClassLoader());

                Class<?>[] realParams = bkDesc.equals(data.getDesc()) ?
                        params : CacheUtil.getParams(clazz.getClassLoader(), data.getDesc());
                return getMethod(target, data.getName2(), key, "缓存", true, realParams);
            } catch (ClassNotFoundException ignored) {
            }
        }

        try {
            Method m = (Method) Class.forName(
                            "cc.zyycc.remap.method.SafeMethodHelper",
                            true, LoaderManager.getClassLoader(MyLoader.AGENT))
                    .getMethod("getMethod", Class.class, String.class, String.class, Class[].class)
                    .invoke(null, clazz, name, bkDesc, params);
            if (m != null) {
                METHOD_CACHE.put(key, m);
                return m;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return getMethod(clazz, name, key, "兜底", false, params);

    }


    private static Method getMethod(Class<?> clazz, String name, String key, String exceptionMessage, boolean failCache, Class<?>... params) throws NoSuchMethodException {
        try {
            Method m = clazz.getDeclaredMethod(name, params);
            METHOD_CACHE.put(key, m);
            return m;
        } catch (NoSuchMethodException ignored) {
            try {
                Method method = clazz.getMethod(name, params);
                METHOD_CACHE.put(key, method);
                return method;
            } catch (NoSuchMethodException e) {
                cache.addFail(key, "来自" + exceptionMessage + " class:" + clazz.getName() + "  getMethod反射映射", failCache);
                throw e;
            }
        } catch (Throwable t) {
            if (t instanceof NoSuchMethodException
                    || t instanceof ClassNotFoundException
                    || t instanceof NoClassDefFoundError) {
               throw t;//....
            }
            throw t;
        }
    }
}
