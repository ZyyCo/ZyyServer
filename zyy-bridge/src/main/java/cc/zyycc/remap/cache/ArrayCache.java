package cc.zyycc.remap.cache;

import cc.zyycc.common.VersionInfo;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArrayCache extends BaseCache<Collection<?>, String> {
    public Map<String, Set<String>> cacheMappingSuccessMap;

    private ArrayCache(String cacheFile) {
        super(Paths.get(VersionInfo.CACHE_DIR, "cache"), cacheFile + ".txt", "",
                new ArrayRule(), false);
        load(this.cacheFile, (rule) -> {
            if (cacheMappingSuccessMap == null) {
                cacheMappingSuccessMap = new ConcurrentHashMap<>();
            }
            cacheMappingSuccessMap.computeIfAbsent(rule.left, k -> ConcurrentHashMap.newKeySet()).addAll(parseArray(rule.right));
        });
    }

    protected static ArrayCache init(String dir, String cacheFileName) {
        return new ArrayCache(dir + "/" + cacheFileName);
    }

    public Map<String, Set<String>> getCacheTable() {
        return this.cacheMappingSuccessMap;
    }

    @Override
    public Collection<?> getSuccessCache(String search) {
        return cacheMappingSuccessMap.get(search);
    }

    @Override
    public Collection<?> getFailCache(String search) {
        return Collections.singleton(cacheMappingError.get(search));
    }

    @Override
    public boolean hasSuccessCache(String search) {
        return cacheMappingSuccessMap != null;
    }

    @Override
    public boolean hasFailCache(String searchEntry) {
        return cacheMappingError.get(searchEntry) != null;
    }

    @Override
    public List<String> get() {
        return new ArrayList<>(cacheMappingError.keySet());
    }


    public Set<String> parseArray(String str) {
        int s1 = str.indexOf("[");
        if (s1 == -1) {
            return Collections.singleton(str);
        }
        int s2 = str.indexOf("]", s1 + 1);
        if (s2 == -1) {
            return Collections.singleton(str);
        }
        Set<String> set = ConcurrentHashMap.newKeySet();

        String methodDescs = str.substring(s1 + 1, s2);

        int start = 0;
        while (start < methodDescs.length()) {
            int index = methodDescs.indexOf(",", start);
            if (index != -1) {
                set.add(methodDescs.substring(start, index).trim());
            } else {
                set.add(methodDescs.substring(start).trim());
                return set;
            }
            start = index + 1;
        }
        return set;
    }

}
