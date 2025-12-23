package cc.zyycc.remap.cache;

import cc.zyycc.common.VersionInfo;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrCache extends BaseCache<String, String> {
    public final Map<String, String> cacheMap = new ConcurrentHashMap<>();

    public StrCache(String fileName) {
        super(Paths.get(VersionInfo.CACHE_DIR, "cache"), fileName + ".txt", null, new DefaultRule(), false);
    }


    @Override
    public String getSuccessCache(String search) {
        return cacheMap.get(search);
    }


    @Override
    public String getFailCache(String search) {
        return "";
    }

    @Override
    public boolean hasSuccessCache(String search) {
        return cacheMap.containsKey(search);
    }

    @Override
    public boolean hasFailCache(String searchEntry) {
        return false;
    }



    @Override
    public List<String> get() {
        return null;
    }
}
