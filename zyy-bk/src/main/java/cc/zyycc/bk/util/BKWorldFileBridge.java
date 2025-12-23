package cc.zyycc.bk.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BKWorldFileBridge {
    public static final Map<String, File> worldFilePath = new ConcurrentHashMap<>();

    public static String resolveFile(File container) {
        if (worldFilePath.containsKey(container.getName())) {
            return worldFilePath.get(container.getName()).toString();
        }
        return container.toString();
    }

    public static String resolveFile(File container, String name) {
        if (worldFilePath.containsKey(name)) {
            return worldFilePath.get(name).toString();
        }
        return new File(container, name).getAbsoluteFile().toString();
    }
}
