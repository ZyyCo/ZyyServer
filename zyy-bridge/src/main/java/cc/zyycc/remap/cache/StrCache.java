package cc.zyycc.remap.cache;


import cc.zyycc.common.cache.BaseCache;
import cc.zyycc.common.cache.DefaultRule;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrCache extends BaseCache<String, String> {
    public final Map<String, String> cacheMap = new ConcurrentHashMap<>();

    public StrCache(String fileName) {
        super(Paths.get(".zyy"), fileName + ".txt", null, new DefaultRule(), false);
        load(cacheFile, Rule.CUSTOM);
    }

    public boolean hasCache() {
        return !cacheMap.isEmpty();
    }

    @Override
    public String getSuccessCache(String search) {
        return cacheMap.get(search);
    }

    public String getCacheToFile(String search) {
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
}
