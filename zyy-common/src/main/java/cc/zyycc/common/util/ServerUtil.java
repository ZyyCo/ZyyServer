package cc.zyycc.forge;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ServerUtil {
    public static String[] loadClassPath(Path jarPath) {
        try {
            Attributes mainAttrs = readManifestAttributes(jarPath);
            if (mainAttrs == null) {
                return new String[0];
            }

            String classPath = mainAttrs.getValue("Class-Path");

            return classPath.split(" ");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[0];
    }
    public static Attributes readManifestAttributes(Path jarPath) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();
        }
    }

    /**
     * 如果目标文件不存在，则从 jar 内部资源复制出来
     *
     * @param resourcePathInJar 资源在 jar 包内的路径，例如 "/defaultconfigs/fml.toml"
     * @param targetFilePath    要写出的文件路径，例如 Paths.get("config/fml.toml")
     */
    public static void createFileIfNotExists(String resourcePathInJar, Path targetFilePath) {
        if (Files.exists(targetFilePath)) {
            return;
        }

        try (InputStream in = ServerUtil.class.getResourceAsStream(resourcePathInJar)) {
            if (in == null) {
                System.err.println("❌ 无法从 jar 内加载资源: " + resourcePathInJar);
                return;
            }

            // 确保父目录存在
            Files.createDirectories(targetFilePath.getParent());

            // 拷贝资源到目标文件
            Files.copy(in, targetFilePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("❌ 创建文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 如果目标文件不存在，则用给定内容创建文件
     *
     * @param targetPath 目标文件路径
     * @param content    要写入的内容
     */

    public static void createFileIfAbsent(Path targetPath, String content) {
        if (Files.exists(targetPath)) {
            return;
        }

        try {
            Files.createDirectories(targetPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(targetPath, StandardCharsets.UTF_8)) {
                writer.write(content);
            }
        } catch (IOException e) {
            System.err.println("❌ 创建文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
