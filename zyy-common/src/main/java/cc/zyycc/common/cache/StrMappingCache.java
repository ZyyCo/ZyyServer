package cc.zyycc.remap.cache;

import java.nio.file.Path;


public class StrMappingCache<C> extends BaseCache<String> {
    public StrMappingCache(Path cacheFile, Path cacheFailFile) {
        super(cacheFile, cacheFailFile);
    }

    @Override
    public String getSuccessCache(String search) {
        return cacheMappingSuccessMap.get(search);
    }

    @Override
    public String getFailCache(String search) {
        return cacheMappingError.get(search);
    }

    @Override
    public boolean hasSuccessCache(String search) {
        return cacheMappingSuccessMap.get(search) != null;
    }

    @Override
    public boolean hasFailCache(String searchEntry) {
        return cacheMappingError.containsKey(searchEntry);
    }


}
