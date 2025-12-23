package cc.zyycc.agent.plugin.scan;


import cc.zyycc.common.VersionInfo;
import cc.zyycc.remap.MappingHelper;
import cc.zyycc.util.NmsDetector;

import java.util.Set;


public class SuperClassHelper {


    private static String getSuperPluginClass(String className) {
        if (NmsDetector.isBkNms(className)) {
            return null;
        }
        className = JarScanInfo.getSuperClass(className);
        if (NmsDetector.isBkNms(className)) {
            return null;
        }
        return className;
    }

    /**
     * 获取字段的源类
     * nms类需另外remap处理
     */
    public static String getFieldClassSource(String className, String field) {
        String superNMSClass = getSuperNMSClass(className);
        if (superNMSClass == null) {//noSuperNMSclass
            return className;
        }
        Set<String> set = JarScanInfo.getFields(className);
        if (set == null) {
            String superPluginClass = className;
            while (true) {
                superPluginClass = getSuperPluginClass(superPluginClass);
                if (superPluginClass == null) {//superClass is nmsClass?
                    return superNMSClass;
                }
                Set<String> fields = JarScanInfo.getFields(superPluginClass);
                if (fields != null && fields.contains(field)) {
                    return superPluginClass;
                }
            }
        }
        return set.contains(field) ? className : superNMSClass;
    }


    public static boolean pluginClassHasDeclaredField(String className, String field) {
        if (getSuperNMSClass(className) == null) {//noSuperNMSclass
            return true;
        }
        Set<String> set = JarScanInfo.getFields(className);
        if (set == null) {
            String superPluginClass = className;
            while (true) {
                superPluginClass = getSuperPluginClass(superPluginClass);
                if (superPluginClass == null) {//superClass is nmsClass?
                    return true;
                }
                Set<String> fields = JarScanInfo.getFields(superPluginClass);
                if (fields != null && fields.contains(field)) {
                    return true;
                }
            }
        }
        return set.contains(field);
    }


    public static String getMethodClassSource(String className, String method, String desc) {
        String superNMSClass = getSuperNMSClass(className);
        if (superNMSClass == null) {//noSuperNMSclass
            return className;
        }
        Set<String> set = JarScanInfo.getMethods(className);
        if (set == null) {
            return superNMSClass;
        }

        return set.contains(method + '#' + desc) ? className : superNMSClass;
    }


    public static boolean hasDeclaredMethods(String className, String method, String desc) {
        String cur = className;
        while (cur != null) {
            Set<String> methods = JarScanInfo.getMethods(cur);
            if (methods != null && methods.contains(method + '#' + desc)) {
                return true;
            }
            cur = JarScanInfo.getSuperClass(cur);
        }
        return false;
    }


    public static boolean hasSuperNMSClass(String className) {
        String search = className;
        while (true) {
            String superClass = JarScanInfo.getSuperClass(search);
            if (superClass == null) {
                return false;
            }
            if (superClass.startsWith("net/minecraft/server/" + VersionInfo.BUKKIT_VERSION)) {
                return true;
            } else {
                search = superClass;
            }
        }
    }


    public static String getSuperClass(String className) {
        String superClass = JarScanInfo.getSuperClass(className);
        if (superClass == null || NmsDetector.isBkNms(className)) {
            return null;
        }
        return superClass;
    }

    public static void checkOrRemove(String className) {
        if (!hasSuperNMSClass(className)) {
            JarScanInfo.remove(className);
        }
    }

    public static String getSuperNMSClass(String className) {
        if (NmsDetector.isBkNms(className)) {
            return className;
        }
        String superClass = className;
        while (true) {
            superClass = JarScanInfo.getSuperClass(superClass);
            if (superClass == null) {
                return null;
            }
            if (NmsDetector.isBkNms(superClass)) {
                return superClass;
            }
        }
    }

    public static void checkOrRemove1(String className) {
        Set<String> fields = JarScanInfo.getFields(className);
        if (fields != null) {
            fields.removeIf(field -> {
                if (!MappingHelper.hasMappingField(field)) {//混淆表没有nms字段百分百是自己的字段
                    return false;
                }
                String superClass = className;
                while (true) {
                    superClass = JarScanInfo.getSuperClass(superClass);
                    if (superClass == null) {
                        return false;
                    }
                    Set<String> superFields;
                    if (NmsDetector.isBkNms(superClass)) {
                        superFields = MappingHelper.getBKClassFields(superClass);
                    } else {
                        superFields = JarScanInfo.getFields(superClass);
                    }
                    if (superFields == null) {
                        return true;
                    }
                    for (String superField : superFields) {
                        if (superField.equals(field)) {
                            return false;
                        }
                    }
                    if (NmsDetector.isBkNms(className)) {
                        return false;
                    }
                }
            });
        }
        Set<String> methodDescs = JarScanInfo.getMethods(className);
        if (methodDescs != null) {
            methodDescs.removeIf(methodDesc -> {
                if (!MappingHelper.hasMappingMethod(methodDesc)) {
                    return false;
                } else {
                    String superClass = className;
                    while (true) {
                        superClass = JarScanInfo.getSuperClass(superClass);
                        if (superClass == null) {
                            return false;
                        }
                        if (!NmsDetector.isBkNms(superClass)) {
                            Set<String> superMethods = JarScanInfo.getMethods(superClass);
                            if (superMethods.contains(methodDesc)) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            });
        }
        if (fields == null || fields.isEmpty()) {
            JarScanInfo.removeField(className);
        }
        if (methodDescs == null || methodDescs.isEmpty()) {
            JarScanInfo.removeMethod(className);
        }


    }


//    public static void checkOrRemove2(String className) {
//
//        Set<String> fields = JarScanInfo.getFields(className);
//        if (fields != null) {
//            fields.removeIf(field -> {
//                if (!MappingHelper.hasMappingField(field)) {//混淆表没有nms字段百分百是自己的字段
//                    return false;
//                }
//                String superClass = className;
//                while (true) {
//                    superClass = JarScanInfo.getSuperClass(superClass);
//                    if (superClass == null) {
//                        return false;
//                    }
//                    Set<String> superFields;
//                    if (NmsDetector.isBkNms(superClass)) {
//                        superFields = MappingHelper.getBKClassFields(superClass);
//                    } else {
//                        superFields = JarScanInfo.getFields(superClass);
//                    }
//                    if (superFields == null) {
//                        //都是些bukkit其他版本的nms类,删除即可
//                        return true;
//                    }
//                    for (String superField : superFields) {
//                        if (superField.equals(field)) {
//                            return false;
//                        }
//                    }
//                    if (NmsDetector.isBkNms(className)) {
//                        return false;
//                    }
//                }
//            });
//        }
//
//        Set<String> methodDescs = JarScanInfo.getMethods(className);
//        if (methodDescs != null) {
//            methodDescs.removeIf(MappingHelper::hasMappingMethod);
//        }
//        if (fields == null || fields.isEmpty()) {
//            JarScanInfo.getFieldsCache().remove(className);
//        }
//        if (methodDescs == null || methodDescs.isEmpty()) {
//            JarScanInfo.getMethodsCache().remove(className);
//        }
//    }


    public static Set<String> getParentClassMethods(String className) {
        String superClass = JarScanInfo.getSuperClass(className);
        if (superClass == null) {
            return null;
        }
        return JarScanInfo.getFields(superClass);
    }

}
