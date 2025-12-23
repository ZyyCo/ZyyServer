package cc.zyycc.remap.cache;


import cc.zyycc.common.VersionInfo;
import cc.zyycc.remap.BaseEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MappingCache<C extends BaseEntry> extends BaseCache<BaseEntry, C> {

    protected MappingCache(String cacheFileName, String cacheFailFileName) {
        super(Paths.get(VersionInfo.CACHE_DIR, "cache"),
                cacheFileName + ".txt",
                cacheFailFileName + ".txt");
    }

    protected static <C extends BaseEntry> MappingCache<C> init(String dir, String cacheFileName, String cacheFailFileName) {
        return new MappingCache<>(dir + "/" + cacheFileName, dir + "/" + cacheFailFileName);
    }

    protected static <C extends BaseEntry> MappingCache<C> initMapping(String cacheFileName, String cacheFailFileName) {
        return new MappingCache<>("mappings/" + cacheFileName, "mappings/" + cacheFailFileName);
    }


    public void addSuccessCache(C entry, C entry2) {
        super.addSuccess(entry.generate(), entry2.generate());
    }

    @Override
    public C getSuccessCache(BaseEntry search) {
        String s = cacheMappingSuccessMap.get(search.generate());
        if (s == null) {
            return null;
        }
        return (C) search.recreate(s);
    }

    public BaseEntry getFailCache(BaseEntry search) {
        String s = cacheMappingError.get(search.generate());
        if (s == null) {
            return null;
        }
        System.err.println("[SimpleRemapper] Fail to " + search.getClassName() + "/" + search.getName2());
        return search.recreate(s);
    }


    @Override

    public boolean hasSuccessCache(BaseEntry search) {
        return cacheMappingSuccessMap.get(search.generate()) != null;
    }


    public boolean hasFailCache(BaseEntry searchEntry) {
        return cacheMappingError.containsKey(searchEntry.generate());
    }

    @Override
    public List<C> get() {
        List<C> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : cacheMappingSuccessMap.entrySet()) {
            BaseEntry baseEntry = new BaseEntry(entry.getKey(), entry.getValue());
            list.add((C) baseEntry);
        }
        return list;
    }


    public void removeMappingDirectory() {
        try {
            Path dir = cacheFile.getParent();
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


