package cc.zyycc.agent.plugin;


import cc.zyycc.agent.plugin.scan.SuperClassPreloader;
import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.MappingUtil;
import cc.zyycc.remap.MappingHelper;
import cc.zyycc.remap.cache.MappingCacheHelper;
import cc.zyycc.remap.cache.MappingCacheManager;
import cc.zyycc.remap.cache.MappingCacheTable;
import cc.zyycc.remap.cache.MappingCache;
import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.remap.method.MethodMappingEntry;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleRemapper {

//    public static final AtomicReference<String> alreadyRemap = new AtomicReference<>();
    protected static final MappingCache<MethodMappingEntry> cacheMethod = MappingCacheManager.METHOD;
    private static final MappingCache<BaseEntry> cacheField = MappingCacheManager.FIELD;

    private final ClassLoader loader;
    protected final String currentClassName;

    public SimpleRemapper(String currentClassName, ClassLoader loader) {
        this.loader = loader;
        this.currentClassName = currentClassName;

    }

    public String map(String internalName) {
        if (internalName == null) return null;
        return MappingHelper.getMappingClass(internalName);
    }


    public String mapType(String internalName) {
        if (internalName == null) return null;
        if (internalName.startsWith("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
            return internalName.replace("org/bukkit/craftbukkit/libs/", "");
        }
        if (internalName.contains("net/minecraft/server/v1_")) {

            String remapped = MappingHelper.remapClass(internalName).orElse(null);
            if (remapped == null) {
                if (internalName.startsWith("[L") && internalName.endsWith(";")) {
                    String substring = internalName.substring(2, internalName.length() - 1);
                    String mappingClass = MappingHelper.getMappingClass(substring);
                    return "[L" + mappingClass + ";";
                } else if (internalName.contains("<")) {
                    return MappingUtil.genericParse(internalName, MappingManager.getMappingClasses());
                }
            } else {
                return remapped;
            }
        }
        return internalName;

    }


    public String methodDesc(String methodDesc) {
        if (methodDesc == null) return null;
        if (methodDesc.contains("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
            methodDesc = methodDesc.replace("org/bukkit/craftbukkit/libs/", "");
        }
        if (methodDesc.contains("Lnet/minecraft/server/v1_")) {
            methodDesc = MappingUtil.mapDesc(methodDesc, MappingManager.getMappingClasses());

        }
        return methodDesc;
    }

    public String mapSignature(String signature) {
        if (signature == null) return null;
        if (signature.contains("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
            signature = signature.replace("org/bukkit/craftbukkit/libs/", "");
        }
        if (signature.contains("Lnet/minecraft/server/v1_")) {
            signature = MappingUtil.mapSignature(signature, MappingManager.getMappingClasses());
        }
        return signature;
    }


    public String mapMethod(MethodMappingEntry searchEntry) {
        MappingCacheTable table = MappingCacheHelper.getCache(cacheMethod, searchEntry);
        if (table.pattern == MappingCacheTable.PATTERN_SUCCESS) {
            return ((MethodMappingEntry) table.getEntry()).getMethodName();
        } else if (table.pattern == MappingCacheTable.PATTERN_FAIL) {
            table.sendError("");
            return null;
        }

        String remapClassName = map(searchEntry.getClassName());

        MethodMappingEntry resultEntry = MappingHelper.findMethodMapping(searchEntry).orElse(null);

        if (resultEntry != null) {
            return resultEntry.getMethodName();
        }
        try {
            Class<?> aClass = Class.forName(remapClassName.replace("/", "."), false, loader);
            resultEntry = MappingUtil.searchInSuperClasses(searchEntry, aClass);
            if (resultEntry != null) {
                cacheMethod.addSuccess(searchEntry.generate(), resultEntry.generate());
                return resultEntry.getMethodName();
            }
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
        return null;
    }


    public String mapDesc(String desc) {
        if (desc.contains("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
            desc = desc.replace("org/bukkit/craftbukkit/libs/", "");
        }
        return MappingUtil.mapDesc(desc, MappingManager.getMappingClasses());
    }

    public String mapFiled(String className, String fieldName) {

        if (fieldName == null) return null;

        String remapField = MappingHelper.remapField(className, fieldName);

        if (remapField == null) {
            //缓存
            BaseEntry mappingEntry = new BaseEntry(className, fieldName);
            MappingCacheTable table = MappingCacheHelper.getCache(cacheField, mappingEntry);
            if (table.pattern == MappingCacheTable.PATTERN_SUCCESS) {
                return table.getEntry().getName2();
            }

            if (table.pattern == MappingCacheTable.PATTERN_FAIL) {
                cacheField.getFailCache(mappingEntry);
                return fieldName;
            }
            BaseEntry entry = MappingHelper.searchFieldInSuperClasses(className, fieldName, loader);
            if (entry != null) {
                cacheField.addSuccess(className + " " + fieldName, entry.getClassName() + " " + entry.getName2());
                return entry.getName2();
            }
            try {//可能没反混淆
                String nmsClass;
                nmsClass = MappingHelper.getMappingClass(className);

                Class<?> aClass = Class.forName(nmsClass.replace("/", "."), false, loader);
                try {
                    aClass.getDeclaredField(fieldName);
                    cacheField.addSuccessCache(mappingEntry, new BaseEntry(nmsClass, fieldName));
                    return fieldName;
                } catch (NoSuchFieldException ignored) {
                    try {
                        Field field = aClass.getField(fieldName);
                        cacheField.addSuccessCache(mappingEntry, new BaseEntry(field.getDeclaringClass().getName(), fieldName));
                        return fieldName;
                    } catch (NoSuchFieldException ignored2) {
                        cacheField.addFail(className + " " + fieldName,
                                "当前Class:" + currentClassName + " 在执行mapFiled searchFieldInSuperClasses时无法获取ForgeClass");
                        return fieldName;
                    }
                }
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                //System.err.println("当前类: " + currentClassName + " 在解析字段" + fieldName + "时 父类是nms，无法初始化。跳过remap");
                //漏网之鱼
                System.out.println("漏网之鱼字段：" + className + " " + fieldName);
                cacheField.addSuccess(className + " " + fieldName, className + " " + fieldName);

                return fieldName;
            }
        }
        return remapField;
    }


}
