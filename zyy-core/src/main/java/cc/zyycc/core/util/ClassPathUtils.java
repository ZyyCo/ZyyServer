package cc.zyycc.core.util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class ClassPathUtils {

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


    public static String loadSPI(Path jarPath, String classPath) {
        try (JarFile forgeJar = new JarFile(jarPath.toFile())) {
            JarEntry entry = forgeJar.getJarEntry("META-INF/services/" + classPath);
            try (InputStream in = forgeJar.getInputStream(entry)) {
                String content = new String(readAllBytes(in), StandardCharsets.UTF_8).trim();
                System.out.println("Found SPI: " + content);
                return content;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, List<String>> loadAllSPI(Path jarPath) {
        Map<String, List<String>> spiMap = new HashMap<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // 只处理 META-INF/services/ 下的文件
                if (name.startsWith("META-INF/services/") && !entry.isDirectory()) {
                    String spiInterface = name.substring("META-INF/services/".length());
                    try (InputStream in = jar.getInputStream(entry)) {
                        List<String> implementations = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                                .lines()
                                .map(String::trim)
                                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                                .collect(Collectors.toList());
                        spiMap.put(spiInterface, implementations);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SPI from " + jarPath, e);
        }
        return spiMap;
    }


    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush(); // 可选：保证所有数据写入
        return buffer.toByteArray();
    }


}
