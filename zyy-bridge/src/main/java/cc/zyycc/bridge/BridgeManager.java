package cc.zyycc.bridge;






import java.util.Map;

public class BridgeManager {
    public static final StaticStorageBridge<Map<String, ClassLoader>> LOADER_REGISTRY = new StaticStorageBridge<>("cc.zyycc.common.loader.LoaderManager", "registerLoaders");
//
//    public static final StaticStorageBridge<MappingMethodCache> METHOD_CACHE =
//            new StaticStorageBridge<>("cc.zyycc.common.mapper.cache.MappingCacheManager", "METHOD");
//
//    public static final StaticStorageBridge<Map<MethodMappingEntry, MethodMappingEntry>> METHOD_SUCCESS_CACHE =
//            new StaticStorageBridge<>(METHOD_CACHE, "cacheSuccessMethod");
//    public static final StaticStorageBridge<Set<MethodMappingEntry>> METHOD_FAIL_CACHE =
//            new StaticStorageBridge<>(METHOD_CACHE, "cacheErrorMethod");
//
//
//    public static final Supplier<StaticStorageBridge<Map<String, String>>> JAR_MAPPING =
//            () -> new StaticStorageBridge<>("cc.zyycc.remap.MappingsResolver", "mapping");
//    public static final Supplier<StaticStorageBridge<Map<String, String>>> MAPPING_CLASSES =
//            () -> new StaticStorageBridge<>(JAR_MAPPING.get(), "classes");
//    public static final Supplier<StaticStorageBridge<Map<String, String>>> MAPPING_FIELDS =
//            () -> new StaticStorageBridge<>(JAR_MAPPING.get(), "fields");
//    public static final Supplier<StaticStorageBridge<Map<MethodMappingEntry, MethodMappingEntry>>> MAPPING_METHODS =
//            () -> new StaticStorageBridge<>("cc.zyycc.remap.CustomJarMapping", "methodMapping");


}
