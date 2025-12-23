package cc.zyycc.common.cache;


import java.nio.file.Paths;


public class StrMappingCache extends BaseCache<String, String> {

    public StrMappingCache(String cacheFile, String cacheFailFile) {
        super(Paths.get(".zyy", "cache/mappings/"), cacheFile + ".txt",
                cacheFailFile + ".txt");
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
