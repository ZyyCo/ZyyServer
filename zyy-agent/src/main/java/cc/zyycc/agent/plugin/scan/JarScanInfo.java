package cc.zyycc.agent.plugin.scan;

import cc.zyycc.remap.cache.MappingCacheManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JarScanInfo {
    private static Map<String, String> CLASS_CACHE;
    private static Map<String, Set<String>> FIELDS_CACHE;
    private static Map<String, Set<String>> METHODS_CACHE;

    private static final List<JarScanUnit> JAR_SCAN = new ArrayList<>();


    public JarScanInfo() {
    }

    public JarScanUnit createScanUnit(Path jar) {
        try {
            JarScanUnit jarScanUnit = new JarScanUnit(md5Hex(Files.readAllBytes(jar)), jar, this);
            JAR_SCAN.add(jarScanUnit);
            return jarScanUnit;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String md5Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCache() {
        for (JarScanUnit jarScanUnit : JAR_SCAN) {
            jarScanUnit.saveCache();
        }
    }


    public boolean compareHash() {
        for (JarScanUnit jarScanUnit : JAR_SCAN) {
            if (!jarScanUnit.compareHash()) {
                return false;
            }
        }
        return MappingCacheManager.PLUGINS_CLASS_SCAN.getCacheSize() == JAR_SCAN.size();
    }


    public static String getSuperClass(String className) {
        if (CLASS_CACHE == null) {
            CLASS_CACHE = new ConcurrentHashMap<>();
            for (JarScanUnit jarScanUnit : JAR_SCAN) {
                CLASS_CACHE.putAll(jarScanUnit.classes);
            }
        }
        return CLASS_CACHE.get(className);
    }

    public static void remove(String className) {
        CLASS_CACHE.remove(className);
        FIELDS_CACHE.remove(className);
        METHODS_CACHE.remove(className);
        JAR_SCAN.forEach(unit -> unit.remove(className));
    }


    public static void removeField(String className) {
        FIELDS_CACHE.remove(className);
        // JAR_SCAN.forEach(jarScanUnit -> jarScanUnit.fields.remove(className));
    }

    public static void removeMethod(String className) {
        METHODS_CACHE.remove(className);
        //JAR_SCAN.forEach(jarScanUnit -> jarScanUnit.methods.remove(className));
    }


    public static Set<String> getFields(String className) {

        return FIELDS_CACHE.get(className);
    }

    public static Set<String> getMethods(String className) {
        return METHODS_CACHE.get(className);
    }

    public static Set<String> getClassKey() {
        checkOrCreate();
        return CLASS_CACHE.keySet();
    }

    public static void printFieldCount(String text) {
        checkOrCreate();
        AtomicInteger count = new AtomicInteger();
        FIELDS_CACHE.values().forEach(set -> count.addAndGet(set.size()));
        System.out.println(text + count.get());
    }
    public static void printMethodCount(String text) {
        checkOrCreate();
        AtomicInteger count = new AtomicInteger();
        METHODS_CACHE.values().forEach(set -> count.addAndGet(set.size()));
        System.out.println(text + count.get());
    }

    protected static void checkOrCreate() {
        if (CLASS_CACHE == null) {
            CLASS_CACHE = new ConcurrentHashMap<>();
            for (JarScanUnit jarScanUnit : JAR_SCAN) {
                CLASS_CACHE.putAll(jarScanUnit.classes);
            }
        }
        if (FIELDS_CACHE == null) {
            FIELDS_CACHE = new ConcurrentHashMap<>();
            for (JarScanUnit jarScanUnit : JAR_SCAN) {
                jarScanUnit.fields.forEach((key, value) -> {
                    FIELDS_CACHE.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).addAll(value);
                });
            }
        }
        if (METHODS_CACHE == null) {
            METHODS_CACHE = new ConcurrentHashMap<>();
            for (JarScanUnit jarScanUnit : JAR_SCAN) {
                jarScanUnit.methods.forEach((key, value) -> {
                    METHODS_CACHE.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).addAll(value);
                });
            }
        }
    }


    public List<JarScanUnit> getJarScan() {
        return JAR_SCAN;
    }

    public void loadCache() {
        for (JarScanUnit jarScanUnit : JAR_SCAN) {
            jarScanUnit.loadCache();
        }
        checkOrCreate();
//        for (JarScanUnit jarScanUnit : JAR_SCAN) {
//            jarScanUnit.classes.clear();
//            jarScanUnit.fields.clear();
//            jarScanUnit.methods.clear();
//        }

    }

    public void removeFile() {
//        for (JarScanUnit jarScanUnit : JAR_SCAN) {
//            jarScanUnit.removeFile();
//        }
//        for (String file : MappingCacheManager.PLUGINS_CLASS_SCAN.getCacheMappingSuccessMap().values()) {
//            try {
//                Files.deleteIfExists(Paths.get(file));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        MappingCacheManager.PLUGINS_CLASS_SCAN.removeMappingDirectory();

    }


    public Map<String, String> getClassCache() {
        return CLASS_CACHE;
    }

    public Map<String, Set<String>> getFieldsCache() {
        return FIELDS_CACHE;
    }

    public Map<String, Set<String>> getMethodsCache() {
        return METHODS_CACHE;
    }
}
