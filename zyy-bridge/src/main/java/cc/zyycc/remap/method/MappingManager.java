package cc.zyycc.remap.method;

import cc.zyycc.util.StrClassName;
import cc.zyycc.remap.CustomJarMapping;
import net.md_5.specialsource.JarMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MappingManager {

    public static final CustomJarMapping mapping = new CustomJarMapping();
    public static final Set<String> bkNMSField = ConcurrentHashMap.newKeySet();
    public static final Set<String> bkNMSMethod = ConcurrentHashMap.newKeySet();
    public static final Set<String> bkNMSMethodName = ConcurrentHashMap.newKeySet();
    public static final Map<String, String> bkNMSMethodNameTable = new ConcurrentHashMap<>();
    public static final Map<String, Set<String>> bkNMSFieldMapTable = new ConcurrentHashMap<>();

    public static void init() {
        try (InputStream in = MappingManager.class.getClassLoader().getResourceAsStream("mappings/remapSrg.srg")) {
            mapping.loadMappingsInEntry(in);
            boolean hasStrClassFile = StrClassName.hasStrClassFile();
            mapping.classes.forEach((k, v) -> {
                        CustomJarMapping.convertedClass.put(v, k);
                        if (!hasStrClassFile) {
                            StrClassName.setStrClass(v);
                        }
                    }
            );
            if (hasStrClassFile) {
                StrClassName.load();
            }else {
                StrClassName.writeCacheToFile();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static Optional<String> getRemapClass(String className) {
        return Optional.ofNullable(mapping.classes.get(className));
    }

    public static Map<String, String> getConvertedClasses() {
        return CustomJarMapping.convertedClass;
    }


    public static JarMapping getMapping() {
        return mapping;
    }

    public static Map<String, String> getMappingClasses() {
        return mapping.classes;
    }

    public static Map<String, String> getMappingFields() {
        return mapping.fields;
    }

    public static Map<MethodMappingEntry, MethodMappingEntry> getMappingMethods() {
        return CustomJarMapping.methodEntry;
    }


}
