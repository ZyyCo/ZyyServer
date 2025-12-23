package cc.zyycc.agent.plugin.scan;

import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.cache.ArrayCache;
import cc.zyycc.remap.cache.MappingCache;
import cc.zyycc.remap.cache.MappingCacheManager;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JarScanUnit {

    private final JarScanInfo info;
    public Map<String, String> classes = new ConcurrentHashMap<>();
    public Map<String, Set<String>> fields = new ConcurrentHashMap<>();
    public final Map<String, Set<String>> methods = new ConcurrentHashMap<>();
    private final String md5;
    private final Path jar;
    private final MappingCache<BaseEntry> superClassesCache;
    private final ArrayCache fieldsCache;
    private final ArrayCache methodsCache;

    public JarScanUnit(String md5, Path jar, JarScanInfo info) {
        this.md5 = md5;
        this.jar = jar;
        this.info = info;
        superClassesCache = MappingCacheManager.createSimpleMappingsCache("prescan/" + md5, "super_classes_cache");
        methodsCache = MappingCacheManager.createArrayMappingsCache("prescan/" + md5, "methods_cache");
        fieldsCache = MappingCacheManager.createArrayMappingsCache("prescan/" + md5, "fields_cache");
    }

    public Path getJar() {
        return jar;
    }

    public void saveCache() {
        fields.forEach((key, value) -> fieldsCache.addSuccess(key, value.toString()));
        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<String, Set<String>> entry : fields.entrySet()) {
            entry.getValue().forEach(value -> {
                count.getAndIncrement();
            });
        }
        System.out.println("保存缓存" + md5 + "字段数量" + count.get());

        MappingCacheManager.PLUGINS_CLASS_SCAN.addSuccess(md5, superClassesCache.getCacheFile().getParent().toString());
        superClassesCache.createCacheFile();
        classes.forEach(superClassesCache::addSuccess);
        for (Map.Entry<String, Set<String>> entry : fields.entrySet()) {
            fieldsCache.addSuccess(entry.getKey(), entry.getValue().toString());
        }
        for (Map.Entry<String, Set<String>> entry : methods.entrySet()) {
            methodsCache.addSuccess(entry.getKey(), entry.getValue().toString());
        }
        classes.clear();
        fields.clear();
        methods.clear();
    }


    public void loadCache() {
        JarScanInfo.checkOrCreate();
        info.getClassCache().putAll(superClassesCache.getCacheMappingSuccessMap());
        superClassesCache.getCacheMappingSuccessMap().clear();


        if(fieldsCache.getCacheTable() != null){
            info.getFieldsCache().putAll(fieldsCache.getCacheTable());
            fieldsCache.getCacheTable().clear();
        }
        if(methodsCache.getCacheTable() != null){
            info.getMethodsCache().putAll(methodsCache.getCacheTable());
            methodsCache.getCacheTable().clear();
        }

        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<String, Set<String>> entry : info.getFieldsCache().entrySet()) {
            entry.getValue().forEach(value -> {
                count.getAndIncrement();
            });
        }
        System.out.println("加载缓存" + md5 + "字段数量" + count.get());
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

    public void remove(String className) {
        int classCount = classes.size();
        classes.remove(className);
        fields.remove(className);
        methods.remove(className);
        if (classCount > classes.size()) {
            System.out.println("删除成功" + className + "现在数量" + classes.size());
        }
        if (classes.isEmpty()) {
            System.out.println("当前" + jar + "数量" + 0);
        }
    }


    public boolean compareHash() {
        return MappingCacheManager.PLUGINS_CLASS_SCAN.hasCacheKey(md5);
    }

    public void removeFile() {
        superClassesCache.removeFile();
        methodsCache.removeFile();
        fieldsCache.removeFile();
    }
}
