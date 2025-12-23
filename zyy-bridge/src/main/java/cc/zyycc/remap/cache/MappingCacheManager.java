package cc.zyycc.remap.cache;


import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.method.MethodMappingEntry;


public class MappingCacheManager {
    public static final MappingCache<MethodMappingEntry> METHOD = MappingCache.initMapping("success_method_mapping", "error_method_mapping");
    public static final MappingCache<MethodMappingEntry> METHOD_REFLECTION = MappingCache.initMapping("success_reflection_method_mapping", "error_reflection_method_mapping");
    public static final MappingCache<BaseEntry> FIELD_REFLECTION =
            MappingCache.initMapping("success_reflection_field_mapping", "error_reflection_field_mapping");
    public static final MappingCache<BaseEntry> FIELD =
            MappingCache.initMapping("success_field_mapping", "error_field_mapping");

    public static final MappingCache<BaseEntry> PLUGINS_CLASS_SCAN =
            MappingCache.initMapping("plugins_md5", "");

//    public static final MappingCache<BaseEntry> superClassesCache =
//            MappingCacheManager.createSimpleMappingsCache(md5, "super_classes_cache");


    public static MappingCache<BaseEntry> createSimpleMappingsCache(String dir, String cacheFileName) {
        return MappingCache.init("mappings/" + dir, cacheFileName, "");
    }

    public static ArrayCache createArrayMappingsCache(String dir, String cacheFileName) {
        return ArrayCache.init("mappings/" + dir, cacheFileName);
    }

    public static MappingCache<BaseEntry> createSimpleMappingsCache(String dir, String cacheFileName, String cacheFailFileName) {
        return MappingCache.init("mappings/" + dir, cacheFileName, cacheFailFileName);
    }
}
