package cc.zyycc.remap.cache;

import java.nio.file.Path;
import java.util.List;

public class ArrayCache extends BaseCache<String>{


    public ArrayCache(Path dir, String cacheFile, String cacheFailFile) {
        super(dir, cacheFile, cacheFailFile);
    }

    @Override
    public String getSuccessCache(String search) {
        return "";
    }

    @Override
    public String getFailCache(String search) {
        return "";
    }

    @Override
    public boolean hasSuccessCache(String search) {
        return false;
    }

    @Override
    public boolean hasFailCache(String searchEntry) {
        return false;
    }

    @Override
    public List<String> get() {
        return List.of();
    }
}
