package cc.zyycc.agent;


import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.cache.MappingCache;
import cc.zyycc.remap.method.MethodMappingEntry;


public class MappingCacheManager {
    public static final MappingCache<MethodMappingEntry> METHOD = MappingCache.init("success_method_mapping", "error_method_mapping");
    public static final MappingCache<MethodMappingEntry> METHOD_REFLECTION = MappingCache.init("success_reflection_method_mapping", "error_reflection_method_mapping");
    public static final MappingCache<BaseEntry> FIELD_REFLECTION =
            MappingCache.init("success_reflection_field_mapping", "error_reflection_field_mapping");
    public static final MappingCache<BaseEntry> FIELD =
            MappingCache.init("success_field_mapping", "error_field_mapping");

    public static final MappingCache<BaseEntry> PLUGINS_CLASS_SCAN =
            MappingCache.init("plugins_class_scan", "");
}
