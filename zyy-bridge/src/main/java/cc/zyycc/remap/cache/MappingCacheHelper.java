package cc.zyycc.remap.cache;

import cc.zyycc.remap.BaseEntry;

public class MappingCacheHelper {
    public static <C extends BaseEntry> MappingCacheTable getCache(MappingCache<C> cache, C entry) {
        BaseEntry successCache = cache.getSuccessCache(entry);
        if (successCache == null) {
            boolean failCache = cache.hasFailCache(entry);
            if (failCache) {
                return MappingCacheTable.createFailCacheTable(entry);
            }
            return MappingCacheTable.empty();
        } else {
            return MappingCacheTable.createSuccessCacheTable(successCache);
        }
    }




}
