package cc.zyycc.remap.cache;


import cc.zyycc.util.ConfusionStr;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static cc.zyycc.remap.cache.BaseCache.Rule.FAIL;
import static cc.zyycc.remap.cache.BaseCache.Rule.SUCCESS;

public abstract class BaseCache<R, C> {
    public final Map<String, String> cacheMappingSuccessMap = new ConcurrentHashMap<>();
    public final Map<String, String> cacheMappingError = new ConcurrentHashMap<>();
    protected final Path cacheFile;
    protected Path cacheFailFile;
    protected final Path dir;
    private final CacheRule cacheRule;

    public BaseCache(Path dir, String cacheFile) {
        this(dir, cacheFile, "", new DefaultRule(), true);
    }

    public BaseCache(Path dir, String cacheFile, String cacheFailFile) {
        this(dir, cacheFile, cacheFailFile, new DefaultRule(), true);
    }


    public BaseCache(Path dir, String cacheFileName, String cacheFailFile, CacheRule lineRule, boolean loadDefault) {
        this.dir = dir;
        this.cacheFile = this.dir.resolve(cacheFileName);
        if (cacheFailFile != null) {
            this.cacheFailFile = dir.resolve(cacheFailFile);
        }
        this.cacheRule = lineRule;
        if (loadDefault) {
            load(this.cacheFile, rule -> cacheMappingSuccessMap.put(rule.left, rule.right));
            load(this.cacheFailFile, rule -> cacheMappingSuccessMap.put(rule.left, rule.right));
        }
    }


    public final synchronized void load(Path cacheFile, AddBehavior addBehavior) {
        if (!cacheFile.toFile().exists()) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(cacheFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Rule rule = this.cacheRule.lineRule(line);
                addBehavior.add(rule);
            }
        } catch (IOException e) {
            System.err.println("格式错误");
        }
    }


    public void writeCacheToFile(Path cacheFile, Map<String, String> cacheMap) {
        try {
            Files.createDirectories(cacheFile.getParent());
            try (FileWriter fw = new FileWriter(cacheFile.toFile(), true)) {
                for (Map.Entry<String, String> entry : cacheMap.entrySet()) {
                    String line;
                    if (entry.getValue() == null || entry.getValue().isEmpty()) {
                        line = entry.getKey() + "\n";
                    } else {
                        line = entry.getKey() + " "
                                + entry.getValue() + "\n";
                    }
                    fw.write(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void saveToFile(Path cacheFile, String left, String right) {
        try {

            Files.createDirectories(cacheFile.getParent());
            String line;
            if (right == null || right.isEmpty()) {
                line = left + "\n";
            } else {
                line = left + " "
                        + right + "\n";
            }


            try (FileWriter fw = new FileWriter(cacheFile.toFile(), true)) {
                fw.write(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCacheSize() {
        return cacheMappingSuccessMap.size();
    }


    public boolean hasDirectory() {
        return Files.exists(dir);
    }

    public boolean hasFile() {
        return Files.exists(cacheFile);
    }

    public abstract R getSuccessCache(C search);

    public abstract R getFailCache(C search);


    public abstract boolean hasSuccessCache(C search);

    public abstract boolean hasFailCache(C searchEntry);


    public boolean hasCacheKey(String string) {
        return cacheMappingSuccessMap.containsKey(string);
    }

    public void addSuccess(String str1) {
        cacheMappingSuccessMap.put(str1, "");
        saveToFile(cacheFile, str1, null);
    }

    public void addSuccess(String str1, String str2) {
        cacheMappingSuccessMap.put(str1, str2);
        saveToFile(cacheFile, str1, str2);
    }

    public void addFile(InputStream in) throws IOException {
        Files.createDirectories(cacheFile.getParent());
        try (OutputStream out = Files.newOutputStream(cacheFile.toFile().toPath())) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }


    }


    public void writeFile(String str1, String str2) {
        saveToFile(cacheFile, str1, str2);
    }


    public void addFail(String string, String reason) {
        cacheMappingError.put(string, reason);
        saveToFile(cacheFailFile, string, reason);
        throw new RuntimeException("[RemapFail] " + string + " -> " + reason);
    }

    public abstract List<C> get();

    public Map<String, String> getCacheMappingSuccessMap() {
        return cacheMappingSuccessMap;
    }


    public void createCacheFile() {
        try {
            Files.createDirectories(cacheFile.getParent());
            Files.write(cacheFile, new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeDirectory() {
        try {
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

    public void removeFile() {
        try {
            Files.deleteIfExists(cacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getCacheFile() {
        return cacheFile;
    }

    public static class Rule {
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
        public static final int CUSTOM = 2;
        public final String left;
        public final String right;

        public Rule(String left) {
            this.left = left;
            this.right = null;
        }

        public Rule(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public static Rule empty() {
            return new Rule("");
        }
    }
}
