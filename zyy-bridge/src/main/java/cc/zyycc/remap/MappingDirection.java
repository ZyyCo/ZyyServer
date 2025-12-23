package cc.zyycc.remap;

import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.remap.method.MethodMappingEntry;

import java.util.Map;

public enum MappingDirection {
    BUKKIT_TO_FORGE,
    FORGE_TO_BUKKIT;

    MappingDirection(){

    }

    public static MethodMappingEntry normalizeEntry(MethodMappingEntry entry, MappingDirection direction) {
        if (direction == MappingDirection.FORGE_TO_BUKKIT) {
            entry = transformationToBukkit(entry, MappingManager.getConvertedClasses());
        }
        if (direction == MappingDirection.BUKKIT_TO_FORGE) {
            entry = transformationToForge(entry, MappingManager.getMappingClasses());
        }
        return entry;
    }

    private static MethodMappingEntry transformationToForge(MethodMappingEntry entry, Map<String, String> mappingClasses) {
        return entry;
    }

    public static MethodMappingEntry transformationToBukkit(MethodMappingEntry entry, Map<String, String> mappings) {
        if (MappingUtil.isForgeClass(entry.getClassName())) {
            String bkClass = mappings.get(entry.getClassName());
            if (bkClass != null) {
                entry.setClassName(bkClass);
            }
        }
        if (MappingUtil.isForgeParams(entry.getParams())) {
            String params = MappingUtil.mapDesc(entry.getParams(), mappings);
            entry.setParams(params);
        }
        if (MappingUtil.isForgeParams(entry.getParams())) {
            String params = MappingUtil.mapDesc(entry.getReturnType(), mappings);
            entry.setReturnType(params);
        }

        return entry;
    }

}
