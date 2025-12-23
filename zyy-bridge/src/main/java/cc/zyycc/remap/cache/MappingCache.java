package cc.zyycc.remap.cache;


import cc.zyycc.remap.CustomJarMapping;
import cc.zyycc.remap.MappingHandle;
import cc.zyycc.remap.method.MethodMappingEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MappingMethodCache extends BaseCache<MethodMappingEntry> {
    public static final Map<String, MappingMethodCache> mappingMethodCache = new ConcurrentHashMap<>();

    protected MappingMethodCache(String cacheFileName, String cacheFailFileName) {
        super(Paths.get(".zyy", "cache/mappings/" + cacheFileName + ".txt"),
                Paths.get(".zyy", "cache/mappings/" + cacheFailFileName + ".txt"));
    }

    public static MappingMethodCache init(String cacheFileName, String cacheFailFileName) {
        return new MappingMethodCache(cacheFileName, cacheFailFileName);
    }

    @Override
    public boolean hasSuccessCache(MethodMappingEntry search) {
        return cacheMappingSuccessMap.get(search.generate()) != null;
    }

    @Override
    public boolean hasFailCache(MethodMappingEntry searchEntry) {
        return cacheMappingError.contains(searchEntry.generate());
    }

    @Override
    protected int count() {
        return 4;
    }

    @Override
    protected int leftIndex() {
        return 0;
    }

    @Override
    protected int rightIndex() {
        return 2;
    }

}


