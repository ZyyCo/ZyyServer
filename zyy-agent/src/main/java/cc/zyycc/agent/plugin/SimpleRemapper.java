package cc.zyycc.agent.enhancer.plugin;


import cc.zyycc.common.mapper.MappingsResolver;
import cc.zyycc.common.mapper.cache.MappingCacheHelper;
import cc.zyycc.common.mapper.cache.MappingCacheManager;
import cc.zyycc.common.mapper.cache.MappingCacheTable;
import cc.zyycc.common.mapper.method.MethodMappingEntry;
import cc.zyycc.common.mapper.method.SafeMethodHelper;
import cc.zyycc.common.util.MapperUtil;

public class SimpleRemapper {
    private final ClassLoader loader;

    public SimpleRemapper(ClassLoader loader) {
        this.loader = loader;
    }

    public String map(String internalName) {
        if (internalName == null) return null;
        return MappingsResolver.remapClass(internalName)
                .orElse(internalName);
    }


    public String methodDesc(String methodDesc) {
        if (methodDesc == null) return null;
        if (methodDesc.contains("Lnet/minecraft/server/v1_")) {
            methodDesc = MapperUtil.methodDesc(methodDesc, MappingsResolver.getMappingClasses());
        }
        return methodDesc;
    }


    public MethodMappingEntry mapMethodEntry(String className, String methodName, String desc) {
        return MethodMappingEntry.createClassName(className);
    }

    public MethodMappingEntry mapMethod(String className, String methodName, String desc) {
        cc.zyycc.agent.enhancer.plugin.MethodMappingEntry searchEntry =
                cc.zyycc.agent.enhancer.plugin.MethodMappingEntry.create(className, methodName, desc);
        MappingCacheTable table = MappingCacheHelper.getCache(MappingCacheManager.METHOD,
                searchEntry, loader);
        if (table.pattern == MappingCacheTable.PATTERN_SUCCESS) {
            return (MethodMappingEntry) table.getEntry();
        }

        if (table.pattern == MappingCacheTable.PATTERN_FAIL) {
            System.err.println("[SimpleRemapper] Fail to " + className + "/" + methodName + " " + desc);
            System.err.println("forge:" + map(className) + "/" + methodName + " " + methodDesc(desc));
            return MethodMappingEntry.create(map(className), methodName, methodDesc(desc));
        }
        String remapClassName = map(className);
        String remapDesc = methodDesc(desc);

        MethodMappingEntry resultEntry;
        try {
            Class.forName(remapClassName.replace("/", "."), false, loader)
                    .getMethod(methodName, MapperUtil.getParams(loader, remapDesc));
            resultEntry = MethodMappingEntry.create(remapClassName, methodName, remapDesc);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException ignored) {
            resultEntry = MappingsResolver.findMethodMapping(searchEntry, loader).orElse(null);
            if (resultEntry == null) {
                try {
                    Class<?> aClass = Class.forName(remapClassName.replace("/", "."), false, loader);
                    resultEntry = SafeMethodHelper.searchInSuperClasses(searchEntry, aClass);
                } catch (ClassNotFoundException | NullPointerException e) {
                    MappingCacheHelper.addFailCache(MappingCacheManager.METHOD, searchEntry);
                    System.err.println("[SimpleRemapper] Fail to " + className + "/" + methodName + " " + desc);
                    System.err.println("forge:" + map(className) + "/" + methodName + " " + methodDesc(desc));
                    return MethodMappingEntry.create(remapClassName, methodName, remapDesc);
                }
            }
        }
        MappingCacheHelper.addSuccessCache(MappingCacheManager.METHOD, searchEntry, resultEntry);
        return resultEntry;

    }


    public String mapDesc(String desc) {
        return MapperUtil.mapDesc(desc, MappingsResolver.getMappingClasses());
    }

    public String mapFiled(String className, String filedName) {
        return MappingsResolver.remapField(className, filedName);
    }

}
