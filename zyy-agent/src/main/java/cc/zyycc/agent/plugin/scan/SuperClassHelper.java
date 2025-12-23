package cc.zyycc.agent.plugin;


import cc.zyycc.remap.MappingHelper;

import java.util.Set;

public class SuperClassHelper {


    public static boolean hasDeclaredFields(String className, String field) {
        String cur = className;

        while (cur != null) {
            Set<String> fields = SuperClassPreloader.fieldsCaches.get(cur);
            if (fields != null && fields.contains(field)) {
                return true;
            }
            cur = SuperClassPreloader.CACHE_CLASS_CACHE.get(cur);
        }
        return false;
    }

    /**
     * 小心接口
     *
     * @param className
     * @param method
     * @param desc
     * @return
     */
    public static boolean hasDeclaredMethods(String className, String method, String desc) {
        String cur = className;
        while (cur != null) {
            Set<String> methods = SuperClassPreloader.methodsCaches.get(cur);
            if (methods != null && methods.contains(method + "#" + desc)) {
                return true;
            }
            cur = SuperClassPreloader.CACHE_CLASS_CACHE.get(cur);
        }
        return false;
    }

    public static String getOrSuperClass(String className) {
        if (className.startsWith("net/minecraft/server/v1_")) {
            return className;
        }
        String search = className;
        while (true) {
            String superClass = SuperClassPreloader.CACHE_CLASS_CACHE.get(search);
            if (superClass == null) {
                return null;
            }
            if (superClass.startsWith("net/minecraft/server/v1_")) {
                return superClass;
            } else {
                search = superClass;
            }
        }
    }


    public static boolean hasNMSClass(String className) {
        String search = className;
        while (true) {
            String superClass = SuperClassPreloader.CACHE_CLASS_CACHE.get(search);
            if (superClass == null) {
                return false;
            }
            if (superClass.startsWith("net/minecraft/server/v1_")) {
                return true;
            } else {
                search = superClass;
            }
        }
    }

    public static void checkOrRemove(String className) {
        if (!hasNMSClass(className)) {
            SuperClassPreloader.CACHE_CLASS_CACHE.remove(className);
            SuperClassPreloader.fieldsCaches.remove(className);
            SuperClassPreloader.methodsCaches.remove(className);
        }
    }


    public static String getSuperClass(String className) {
        return SuperClassPreloader.CACHE_CLASS_CACHE.get(className);
    }

    public static void checkOrRemove2(String className) {
        Set<String> fields = SuperClassPreloader.fieldsCaches.get(className);
        if (fields != null) {
            fields.removeIf(field -> !MappingHelper.hasMappingField(field));
        }

        Set<String> methodDescs = SuperClassPreloader.methodsCaches.get(className);
//
//        for (String methodDesc : methodDescs) {
//            if (MappingHelper.hasMappingMethod(methodDesc)) {
//                methodDescs.remove(methodDesc);
//            }
//        }

        if (methodDescs != null) {
            methodDescs.removeIf(MappingHelper::hasMappingMethod);
        }
        if (fields == null || fields.isEmpty()) {
            SuperClassPreloader.fieldsCaches.remove(className);
        }
        if (methodDescs == null || methodDescs.isEmpty()) {
            SuperClassPreloader.methodsCaches.remove(className);
        }
//        boolean empty = (fields == null || fields.isEmpty())
//                && (methods == null || methods.isEmpty());
//        if (empty) {
//            SuperClassPreloader.fieldsCaches.remove(className);
//            SuperClassPreloader.methodsCaches.remove(className);
//        }
    }

}
