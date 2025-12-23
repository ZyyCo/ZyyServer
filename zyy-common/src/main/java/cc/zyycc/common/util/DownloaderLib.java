package cc.zyycc.installer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Downloader {


    public static void downloadJar(LibraryHandle lh) throws IOException {
        System.out.println("Downloading Librarie:" + lh.groupId + ":" + lh.artifactId + ":" + lh.version);
        String groupPath = lh.groupId.replace('.', '/');

        String jarUrl = String.format(
                "https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.jar",
                groupPath, lh.artifactId, lh.version, lh.artifactId, lh.version
        );

        Files.createDirectories(lh.jarPath.getParent());

        try (InputStream in = new URL(jarUrl).openStream()) {
            Files.copy(in, lh.jarPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Path> downloadExtraMavenJar(String[] libs) throws IOException {
        List<Path> allLibPaths = new ArrayList<>();
        for (String lib : libs) {
            LibraryHandle lh = LibraryHandle.checkMavenLibs(lib);
            if (lh.needDownload) {
                downloadJar(lh);
            }
            allLibPaths.add(lh.jarPath);
        }
        return allLibPaths;
    }


    static class LibraryHandle {
        private static final String ROOT_DIR = "libraries";
        public final String groupId;
        private final String artifactId;
        private final String version;
        boolean needDownload;

        public Path jarPath;


        private LibraryHandle(Path jarPath, String groupId, String artifactId, String version, boolean needDownload) throws IOException {
            this.jarPath = jarPath;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.needDownload = needDownload;

        }

        public static LibraryHandle checkMavenLibs(String mavenStr) throws IOException {
            String[] split = mavenStr.split(":");
            if (split.length != 3) {
                throw new IllegalArgumentException("Invalid Maven coordinates: " + mavenStr);
            }
            String[] split1 = split[0].split("\\.");
            Path path = Paths.get(ROOT_DIR);
            if (split1.length > 0) {
                for (String s : split1) {
                    path = path.resolve(s);
                }
            } else {
                throw new FileNotFoundException("Invalid Maven coordinates: " + mavenStr);
            }
            //版本
            path = path.resolve(split[2]);
            //jar
            path = path.resolve(split[1] + "-" + split[2] + ".jar");
            if (Files.exists(path)) {
                return new LibraryHandle(path, split[0], split[1], split[2], false);
            } else {
                return new LibraryHandle(path, split[0], split[1], split[2], true);
            }
        }

    }
}
