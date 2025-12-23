package cc.zyycc.remap.cache;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cc.zyycc.remap.cache.BaseCache.Rule.FAIL;
import static cc.zyycc.remap.cache.BaseCache.Rule.SUCCESS;

public abstract class BaseCache<C> {
    public final Map<String, String> cacheMappingSuccessMap = new ConcurrentHashMap<>();
    public final Map<String, String> cacheMappingError = new ConcurrentHashMap<>();
    protected final Path cacheFile;
    protected final Path cacheFailFile;

    public BaseCache(Path cacheFile, Path cacheFailFile) {
        this.cacheFile = cacheFile;
        this.cacheFailFile = cacheFailFile;
        load(cacheFile, 0);
        load(cacheFailFile, 1);
    }


    public final synchronized void load(Path cacheFile, int mode) {
        if (!cacheFile.toFile().exists()) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(cacheFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Rule rule = lineRule(line);
                if (mode == SUCCESS) {
                    cacheMappingSuccessMap.put(rule.left, rule.right);
                } else if (mode == FAIL) {
                    cacheMappingError.put(rule.left, rule.right);
                } else {
                    customMode();
                }
            }
        } catch (IOException e) {
            System.err.println("格式错误");
        }
    }

    public void customMode() {
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

    public void addFail(String string, String reason) {
        cacheMappingError.put(string, reason);
        saveToFile(cacheFailFile, string, reason);
        System.err.println("[RemapFail] -> " + reason);
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
        private final String left;
        private final String right;

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
