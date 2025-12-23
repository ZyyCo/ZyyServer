package cc.zyycc.common.cache;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.zyycc.common.cache.BaseCache.Rule.FAIL;
import static cc.zyycc.common.cache.BaseCache.Rule.SUCCESS;

public abstract class BaseCache<R, C> {
    public final Map<String, String> cacheMappingSuccessMap = new ConcurrentHashMap<>();
    public final Map<String, String> cacheMappingError = new ConcurrentHashMap<>();
    protected final Path cacheFile;
    protected final Path cacheFailFile;
    protected final Path dir;
    private final CacheRule cacheRule;


    public BaseCache(Path dir, String cacheFile, String cacheFailFile) {
        this(dir, cacheFile, cacheFailFile, new DefaultRule(), true);
    }


    public BaseCache(Path dir, String cacheFile, String cacheFailFile, CacheRule lineRule, boolean loadDefault) {
        this.dir = dir;
        this.cacheFile = this.dir.resolve(cacheFile);
        this.cacheFailFile = this.dir.resolve(cacheFailFile);
        this.cacheRule = lineRule;
        if (loadDefault) {
            load(this.cacheFile, 0);
            load(this.cacheFailFile, 1);
        }
        if (!loadDefault) {
            System.out.println("文件路径" + cacheFile);
        }

    }


    public final synchronized void load(Path cacheFile, int mode) {
        if (!cacheFile.toFile().exists()) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(cacheFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Rule rule = this.cacheRule.lineRule(line);
                if (mode == SUCCESS) {
                    cacheMappingSuccessMap.put(rule.left, rule.right);
                } else if (mode == FAIL) {
                    cacheMappingError.put(rule.left, rule.right);
                } else {
                    customMode(rule);
                }
            }
        } catch (IOException e) {
            System.err.println("格式错误");
        }
    }

    public void customMode(Rule rule) {
    }

    public synchronized void saveToFile(Path cacheFile, String left, String right) {
        try {

            Files.createDirectories(cacheFile.getParent());

            String line = left + " "
                    + right + "\n";

            try (FileWriter fw = new FileWriter(cacheFile.toFile(), true)) {
                fw.write(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract C getSuccessCache(C search);

    public abstract C getFailCache(C search);


    public abstract boolean hasSuccessCache(C search);

    public abstract boolean hasFailCache(C searchEntry);


    public void addSuccess(String str1, String str2) {
        cacheMappingSuccessMap.put(str1, str2);
        saveToFile(cacheFile, str1, str2);
    }

    public void writeFile(String str1, String str2) {
        saveToFile(cacheFile, str1, str2);
    }

    public void addFail(String string, String reason, boolean save) {
        cacheMappingError.put(string, reason);
        if (save) {
            saveToFile(cacheFailFile, string, reason);
            System.err.println("[RemapFail] -> " + reason);
        }


    }


    public Rule lineRule(String line) {

        int s1 = line.indexOf(' ');            // between token0 and token1
        int s2 = line.indexOf(' ', s1 + 1);    // between token1 and token2
        int s3 = line.indexOf(' ', s2 + 1);    // between token2 and token3

        // Fail case (no space)
        if (s1 == -1) {
            return new Rule(line, null);
        }

        // Fail case (only 2 tokens: oldOwner oldDesc)
        if (s2 == -1) {
            String left = line.substring(0, s1) + " " + line.substring(s1 + 1);
            return new Rule(left, null);
        }

        // Only 3 tokens (rare)
        if (s3 == -1) {
            String left = line.substring(0, s1) + " " + line.substring(s1 + 1, s2);
            String right = line.substring(s2 + 1);
            return new Rule(left, right);
        }

        // Normal 4-token mapping line
        String left = line.substring(0, s1) + " " + line.substring(s1 + 1, s2);
        String right = line.substring(s2 + 1, s3) + " " + line.substring(s3 + 1);

        return new Rule(left, right);
    }

    public static class Rule {
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
        public static final int CUSTOM = 2;
        protected final String left;
        protected final String right;

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
