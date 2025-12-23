package cc.zyycc.common.mapper.cache;

import cc.zyycc.common.mapper.MappingEntry;

public class MappingCacheHelper {
    public static <C extends MappingEntry> MappingCacheTable getCache(MappingCache<C> cache, C entry) {
        C successCache = cache.getSuccessCache(entry);
        if (successCache == null) {
            C failCache = cache.getFailCache(entry);
            if (failCache != null) {
                return MappingCacheTable.createFailCacheTable(entry);
            }
            return MappingCacheTable.empty();
        } else {
            return MappingCacheTable.createSuccessCacheTable(successCache);
        }
    }




    public static <C extends MappingEntry> void addSuccessCache(MappingCache<C> cache, C originalKey, C newEntry) {
        cache.addSuccessCache(originalKey, newEntry);
    }

    public static <C extends MappingEntry> void addFailCache(MappingCache<C> cache, C originalKey) {
        cache.addFailCache(originalKey);
    }

}
