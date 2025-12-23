package cc.zyycc.common.mapper.method;

import cc.zyycc.common.mapper.CustomJarMapping;
import cc.zyycc.common.mapper.MappingDirection;
import cc.zyycc.common.mapper.MappingsResolver;
import cc.zyycc.common.mapper.cache.MappingCacheHelper;
import cc.zyycc.common.mapper.cache.MappingCacheManager;
import cc.zyycc.common.mapper.cache.MappingCacheTable;
import cc.zyycc.common.mapper.cache.MappingMethodCache;
import cc.zyycc.common.util.MapperUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SafeMethodHelper {
    private static final MappingMethodCache cache = MappingCacheManager.METHOD_REFLECTION;

    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException, ClassNotFoundException {
        // 1. 先直接尝试非混淆名
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }
        // Type.getType(String.class)
        //(Ljava/lang/String;)V
        String bkDesc = Type.getMethodDescriptor(Type.VOID_TYPE, paramsToTypes(params));
        //去掉括号
        String bkParams = bkDesc.substring(1, bkDesc.lastIndexOf(")"));
        MethodMappingEntry searchEntry = MethodMappingEntry.createSearchEntry(clazz.getName(), name, bkParams);

        MappingCacheTable table = MappingCacheHelper.getCache(cache,
                searchEntry);
        MethodMappingEntry resultEntry = null;
        if (table.pattern == MappingCacheTable.PATTERN_SUCCESS) {
            resultEntry = (MethodMappingEntry) table.getEntry();
        }
        if (table.pattern == MappingCacheTable.PATTERN_FAIL) {
            //fail
            System.err.println("error cache " + clazz.getName() + "/" + name + " " + Arrays.toString(params));
            throw new NoSuchMethodException(clazz.getName() + "." + name);
        }


        if (resultEntry == null) {
            //转换成bukkit class
            MappingDirection.normalizeEntry(searchEntry, MappingDirection.FORGE_TO_BUKKIT);
            resultEntry = MappingsResolver.findMethodMapping(searchEntry).orElse(null);
        }

        if (resultEntry == null) {
            resultEntry = searchInSuperClasses(searchEntry, clazz);
            if (resultEntry != null) {
                searchEntry.setMethodDesc(resultEntry.getMethodDesc());
                searchEntry.setClassName(clazz.getName());
                MappingCacheHelper.addSuccessCache(cache, searchEntry, resultEntry);
            }
        }

        if (resultEntry != null) {
            try {
                if (!bkParams.equals(resultEntry.getParams())) {
                    params = MapperUtil.getParams(clazz.getClassLoader(), resultEntry.getMethodDesc());
                }
                return clazz.getMethod(resultEntry.getMethodName(), params);
            } catch (NoSuchMethodException ignored) {
            }
        }

        // 3. 尝试 declaredMethod（非 public）
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }
        cache.addFailCache(searchEntry);
        throw new NoSuchMethodException(clazz.getName() + "." + name);

    }

    private static Type[] paramsToTypes(Class<?>[] params) {
        return Arrays.stream(params)
                .map(Type::getType)
                .toArray(Type[]::new);
    }

    public static MethodMappingEntry searchInSuperClasses(MethodMappingEntry searchEntry, Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();
        getSuperClass(clazz, list);
        for (Class<?> aClass : list) {
            String bkClassName = CustomJarMapping.convertedClass.get(aClass.getName().replace(".", "/"));
            if (bkClassName == null) {
                continue;
            }
            MethodMappingEntry searchKey = searchEntry.copyWithClassName(bkClassName);
            return MappingsResolver.findMethodMapping(searchKey)
                    .orElse(MapperUtil.getMethodDescToEntry(aClass, searchEntry.getMethodName(), searchEntry.getParams()));
        }
        return null;
    }

    public static void getSuperClass(Class<?> clazz, List<Class<?>> list) {
        if (clazz == Object.class || clazz == null) {
            return;
        }
        //superClass和 interfaces合并
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            list.add(superClass);
            getSuperClass(superClass, list);
        }
        for (Class<?> anInterface : clazz.getInterfaces()) {
            list.add(anInterface);
            getSuperClass(anInterface, list);
        }

    }


}
