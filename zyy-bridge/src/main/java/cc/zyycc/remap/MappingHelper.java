package cc.zyycc.remap;

import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.remap.method.MethodMappingEntry;

import java.util.*;

public class MappingHelper {

    public static String getMappingClass(String className) {
        return remapClass(className).orElse(className);
    }

    public static Optional<String> remapClass(String className) {
        if (!className.startsWith("net/minecraft/server/v1")) {
            return Optional.empty();
        }
        String fClass = MappingManager.getMappingClasses().get(className);
        if (fClass != null) {
            return Optional.of(fClass);
        }
        return Optional.empty();
    }


    public static Optional<MethodMappingEntry> findMethodMapping(MethodMappingEntry searchEntry) {
        MethodMappingEntry valueEntry = CustomJarMapping.methodEntry.get(searchEntry);
        if (valueEntry != null) {
            return Optional.of(valueEntry);
        }
        return Optional.empty();
    }

    public static Optional<String> getConvertedClass(String className) {
        return Optional.ofNullable(CustomJarMapping.convertedClass.get(className));
    }


    public static String remapField(String className, String fieldName) {
        return MappingManager.getMappingFields().get(className + "/" + fieldName);
    }

    public static boolean hasMappingField(String fieldName) {
        return MappingManager.bkNMSField.contains(fieldName);
    }

    public static boolean hasMappingMethod(String methodName, String methodDesc) {
        return MappingManager.bkNMSMethod.contains(methodName + '#' + methodDesc);
    }

    public static boolean hasMappingMethodName(String methodName) {
        return MappingManager.bkNMSMethodName.contains(methodName);
    }

    public static String getMappingMethodName(String methodName) {
        return MappingManager.bkNMSMethodNameTable.get(methodName);
    }


    public static boolean hasMappingMethod(String methodNameDesc) {
        return MappingManager.bkNMSMethod.contains(methodNameDesc);
    }

    public static BaseEntry searchFieldInSuperClasses(String className, String fieldName, ClassLoader loader) {
        try {
            if (className == null) {
                return null;
            }
            className = MappingHelper.remapClass(className).orElse(className);
            List<Class<?>> list = new ArrayList<>();
            MappingUtil.getSuperClass(Class.forName(className.replace("/", "."), true, loader), list, false);
            for (Class<?> forgeClazz : list) {
                String forgeClassName = forgeClazz.getName().replace(".", "/");
                String bkClass = MappingManager.getConvertedClasses().get(forgeClassName);
                if (bkClass == null) {
                    break;
                }
                String remapField = MappingManager.getMappingFields().get(bkClass + "/" + fieldName);
                if (remapField != null) {
                    return new BaseEntry(forgeClassName, remapField);
                }
            }

            return null;
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }


    public static String getMappingField(String className, String fieldName) {
        return MappingManager.getMappingFields().get(className + "/" + fieldName);
    }

    public static Set<String> getBKClassFields(String className) {
        return MappingManager.bkNMSFieldMapTable.get(className);
    }

    public static boolean hasBKClassField(String className, String fieldName) {
        return MappingManager.bkNMSFieldMapTable.getOrDefault(className, Collections.emptySet()).contains(fieldName);
    }


}
