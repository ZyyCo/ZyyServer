package cc.zyycc.agent.transformer.scan;

import cc.zyycc.remap.BaseEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JarScanUnit {
    private final Path jar;
    public String md5;
    private byte[] bytes;
    public final Map<String, String> cacheClasses = new ConcurrentHashMap<>();
    public final Map<String, Set<String>> fieldsCaches = new ConcurrentHashMap<>();
    public final Map<String, Set<String>> methodsCaches = new ConcurrentHashMap<>();


    public JarScanUnit(Path jar) {
        this.jar = jar;
        try {
            this.bytes = Files.readAllBytes(jar);
            this.md5 = generateMd5();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String getMd5() {
        return md5;
    }

    public String generateMd5() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean hashCompare(byte[] bytes) {
        try {


            return SCAN.hasSuccessCache(new BaseEntry(hash));
        } catch (NoSuchAlgorithmException ignored) {

        }
        return false;

    }

    public Path getJar() {
        return jar;
    }
}
