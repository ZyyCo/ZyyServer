package cc.zyycc.common.util;

import cc.zyycc.common.VersionInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static final Map<String, Path> CACHE_PATH = new HashMap<>();

    static {
        try {
            extractJar("zyy-bk.jar", "bkao.jar");
            extractJar("zyy.jar", "zyyaruzi.jar");
            //core
            extractJar("zyy-core.jar", "nashicore.jar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getCacheJar(String cacheDir) {
        if (cacheDir.endsWith(".jar")) {
            cacheDir = cacheDir.substring(0, cacheDir.length() - 4);
        }
        return CACHE_PATH.get(cacheDir);
    }


    public static Path extractJar(String internalPathInJar, String cacheDir) throws IOException {
        if (!internalPathInJar.endsWith(".jar")) {
            internalPathInJar = internalPathInJar + ".jar";
        }
        String pathInJar = "/" + VersionInfo.INTERNALPATH + "/" + internalPathInJar;

        try (InputStream is = FileManager.class.getResourceAsStream(pathInJar)) {
            if (is == null) {
                throw new FileNotFoundException("没找到内嵌核心资源: " + pathInJar);
            }

            Files.createDirectories(Paths.get(VersionInfo.CACHE_DIR));
            Path path = Paths.get(VersionInfo.CACHE_DIR, cacheDir);
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            CACHE_PATH.put(cacheDir.substring(0, cacheDir.length() - 4), path);//去掉.jar
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
