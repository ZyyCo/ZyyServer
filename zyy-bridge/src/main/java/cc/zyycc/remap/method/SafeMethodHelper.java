package cc.zyycc.remap.method;


import cc.zyycc.remap.MappingUtil;
import cc.zyycc.remap.MappingDirection;
import cc.zyycc.remap.MappingHelper;
import cc.zyycc.remap.cache.*;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import java.util.Arrays;


public class SafeMethodHelper {


    private static final MappingCache<MethodMappingEntry> cache = MappingCacheManager.METHOD_REFLECTION;

    public static Method getMethod(Class<?> clazz, String name, String bkDesc, Class<?>... params) {
        // Type.getType(String.class)
        //(Ljava/lang/String;)V
        if (!bkDesc.contains("net/minecraft/server/v") && bkDesc.contains("net/minecraft")) {
            bkDesc = MappingUtil.mapDesc(bkDesc, MappingManager.getConvertedClasses());
        }
        //去掉括号
        String bkParams = bkDesc.substring(1, bkDesc.lastIndexOf(")"));
        //创建查找entry
        MethodMappingEntry searchEntry = MethodMappingEntry.createSearchEntry(clazz.getName(), name, bkParams);

        //转换成bukkit class
        MappingDirection.normalizeEntry(searchEntry, MappingDirection.FORGE_TO_BUKKIT);
        MethodMappingEntry resultEntry = MappingHelper.findMethodMapping(searchEntry).orElse(null);
        if (resultEntry != null) {
            try {
                Class<?>[] newParams = resultEntry.getNewParams(searchEntry, params, clazz.getClassLoader());
                return clazz.getDeclaredMethod(resultEntry.getMethodName(), newParams);
            } catch (NoSuchMethodException ignored) {
            }
//            return resultEntry.anewExecuteMethod(clazz.getClassLoader(), params);
        }
        //如果还是找不到继承查找
        resultEntry = MappingUtil.searchInSuperClasses(searchEntry, clazz);
        if (resultEntry != null) {
            //search不需要return 需参数即可 因为无法获取returnType
            searchEntry.setClassName(clazz.getName());
            cache.addSuccessCache(searchEntry, resultEntry);
            return returnMethod(clazz, resultEntry, params, bkParams);
        }
        return null;
    }

    private static Type[] paramsToTypes(Class<?>[] params) {
        return Arrays.stream(params)
                .map(Type::getType)
                .toArray(Type[]::new);
    }

    private static Method returnMethod(Class<?> clazz, MethodMappingEntry resultEntry, Class<?>[] params, String bkParams) {
        if (!bkParams.equals(resultEntry.getParams())) {
            try {
                params = MappingUtil.getParams(clazz.getClassLoader(), resultEntry.getMethodDesc());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultEntry.anewExecuteMethod(clazz.getClassLoader(), params);
    }


}
