package cc.zyycc.common.mapper;

import cc.zyycc.common.mapper.method.MethodMappingEntry;
import cc.zyycc.common.util.MapperUtil;

import java.util.Map;

public enum MappingDirection {
    BUKKIT_TO_FORGE,
    FORGE_TO_BUKKIT;

    MappingDirection(){

    }

    public static MethodMappingEntry normalizeEntry(MethodMappingEntry entry, MappingDirection direction) {
        if (direction == MappingDirection.FORGE_TO_BUKKIT) {
            entry = transformationToBukkit(entry, MappingsResolver.getConvertedClass());
        }
        if (direction == MappingDirection.BUKKIT_TO_FORGE) {
            entry = transformationToForge(entry, MappingsResolver.getMappingClasses());
        }
        return entry;
    }

    private static MethodMappingEntry transformationToForge(MethodMappingEntry entry, Map<String, String> mappingClasses) {
        return entry;
    }

    public static MethodMappingEntry transformationToBukkit(MethodMappingEntry entry, Map<String, String> mappings) {
        if (MapperUtil.isForgeClass(entry.getClassName())) {
            String bkClass = mappings.get(entry.getClassName());
            if (bkClass != null) {
                entry.setClassName(bkClass);
            }
        }
        if (MapperUtil.isForgeParams(entry.getParams())) {
            String params = MapperUtil.mapDesc(entry.getParams(), mappings);
            entry.setParams(params);
        }
        if (MapperUtil.isForgeParams(entry.getParams())) {
            String params = MapperUtil.mapDesc(entry.getReturnType(), mappings);
            entry.setReturnType(params);
        }

        return entry;
    }

}
