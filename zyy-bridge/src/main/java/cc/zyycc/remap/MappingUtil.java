package cc.zyycc.remap;


import cc.zyycc.common.cache.StrMappingCache;
import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.remap.method.MethodMappingEntry;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MappingUtil {


    public static Class<?>[] getParams(ClassLoader loader, String desc) throws ClassNotFoundException {
        String params = desc.substring(desc.indexOf('(') + 1, desc.indexOf(')'));
        Type[] argumentTypes = Type.getArgumentTypes("(" + params + ")V");

        return convert(loader, argumentTypes);
    }


    public static MethodMappingEntry getMethodDescToEntry(Class<?> clazz, String methodName, String params) {
        try {
            Type[] argumentTypes = Type.getArgumentTypes("(" + params + ")V");
            Method method = clazz.getMethod(methodName, convert(clazz.getClassLoader(), argumentTypes));
            String methodDescriptor = Type.getMethodDescriptor(method);
            return MethodMappingEntry.createMethodMappingEntry(clazz.getName(), methodName, params, method.getReturnType().getName(), methodDescriptor);
        } catch (Exception ignored) {
            return null;
        }

    }

    public static Class<?>[] convert(ClassLoader loader, Type[] argumentTypes) throws ClassNotFoundException {
        Class<?>[] params = new Class<?>[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            Type type = argumentTypes[i];
            switch (type.getSort()) {
                case Type.BOOLEAN:
                    params[i] = boolean.class;
                    break;
                case Type.BYTE:
                    params[i] = byte.class;
                    break;
                case Type.CHAR:
                    params[i] = char.class;
                    break;
                case Type.SHORT:
                    params[i] = short.class;
                    break;
                case Type.INT:
                    params[i] = int.class;
                    break;
                case Type.LONG:
                    params[i] = long.class;
                    break;
                case Type.FLOAT:
                    params[i] = float.class;
                    break;
                case Type.DOUBLE:
                    params[i] = double.class;
                    break;
                case Type.ARRAY:
                case Type.OBJECT:

                    String name = type.getClassName();
                    params[i] = Class.forName(name, false, loader);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
        return params;
    }

    public static boolean isForgeClass(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return name.startsWith("net/minecraft/") && !name.contains("server/v1_");
    }

    public static boolean isForgeParams(String params) {
        return params.contains("Lnet/minecraft/") && !params.contains("server/v1_");
    }


    public static String mapSignature(String desc, String prefix, int offest, Map<String, String> mappings) {
        if (desc.length() < 3) {
            return desc;
        }
        StringBuilder out = new StringBuilder(desc.length());
        int lNetIndex = desc.indexOf(prefix, 0);
        if (lNetIndex == -1) {
            return desc;
        }
        int start = 0;
        while (start < desc.length()) {
            char c = desc.charAt(start++);
            if (start <= lNetIndex) {
                out.append(c);
                continue;
            }
            if (c == ';' || c == '<' || c == '>') {
                // String offsetStr = desc.substring(lNetIndex, lNetIndex + offset);
                String bkClass = desc.substring(lNetIndex + 1, start - 1);
                String fClass = mappings.get(bkClass);
                if (fClass != null) {
                    //out.append(offsetStr).append(fClass);
                    out.append("L").append(fClass);
                } else {
                    out.append("L").append(bkClass);
                }
                lNetIndex = desc.indexOf(prefix, start);
                if (lNetIndex != -1) {
                    out.append(c);
                } else {
                    start--;
                    lNetIndex = desc.length();
                }
            }
        }
        return out.toString();
    }


    /**
     * 不带括号
     *
     * @return params
     */
    public static String mapDesc(String desc, Map<String, String> mappings) {
        int lNetIndex = desc.indexOf("Lnet", 0);
        if (lNetIndex == -1) {
            return desc;
        }
        StringBuilder out = new StringBuilder();
        int start = 0;
        while (start < desc.length()) {
            char c = desc.charAt(start++);
            if (start <= lNetIndex) {
                out.append(c);
                continue;
            }
            if (c == ';') {
                String bkClass = desc.substring(lNetIndex + 1, start - 1);
                String fClass = mappings.getOrDefault(bkClass, bkClass);
                out.append("L").append(fClass);
                lNetIndex = desc.indexOf("Lnet", start);
                if (lNetIndex != -1) {
                    out.append(";");
                } else {
                    start--;
                    lNetIndex = desc.length();
                }
            }
        }
        return out.toString();
    }


    public static String mapSignature(String desc, Map<String, String> mappings) {
        if (desc == null) {
            return null;
        }
        if (desc.length() < 6) {
            return desc;
        }
        return mapSignature(desc, "Lnet", 1, mappings);
//        if (desc.charAt(0) == '(') {
//
//        } else {
//            int index = desc.indexOf('<');
//            if (index != -1) {
//                return genericParse(desc, mappings);
//            }
//        }
//        return desc;
    }


    public static String genericParse(String desc, Map<String, String> mappings) {
        //Ljava/util/Map<Ljava/lang/Class<*>;Lnet/minecraft/server/v1_16_R3/EntityTypes<*>;>;
        //Lnet/minecraft/server/v1_16_R3/DataWatcherObject<Ljava/lang/Boolean;>;
        //Lcom/google/common/base/Function<Lnet/minecraft/server/v1_16_R3/NavigationAbstract;Ljava/lang/Boolean;>;
        //Lcom/google/common/collect/BiMap<Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/server/v1_16_R3/EntityTypes;>;
        int start = 0;
        int tail = 0;
        int len = desc.length();
        while (tail < len) {
            int lNetIndex = desc.indexOf("Lnet", start);
            if (lNetIndex == -1) {
                break;
            }
            // 找到后面的 ';'或'<'
            char c = desc.charAt(lNetIndex + tail);
            if (c == '<' || c == '>' || c == ';') {
                String className = desc.substring(lNetIndex + 1, tail + lNetIndex);
                String fClass = mappings.get(className);
                if (fClass != null) {
                    desc = desc.replace(className, fClass);
                }
                start = tail + lNetIndex;
                tail = 0;
            }
            tail++;
        }

        return desc;
    }

    public static MethodMappingEntry searchInSuperClasses(MethodMappingEntry searchEntry, Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();
        getSuperClass(clazz, list, true);
        for (Class<?> aClass : list) {
            String bkClassName = MappingManager.getConvertedClasses().get(aClass.getName().replace(".", "/"));
            if (bkClassName == null) {
                continue;
            }
            MethodMappingEntry searchKey = searchEntry.copyWithClassName(bkClassName);
            MethodMappingEntry methodMappingEntry = MappingHelper.findMethodMapping(searchKey)
                    .orElse(MappingUtil.getMethodDescToEntry(aClass, searchEntry.getMethodName(), searchEntry.getParams()));
            if (methodMappingEntry != null) {
                return methodMappingEntry;
            }
        }
        return null;
    }

    public static void getSuperClass(Class<?> clazz, List<Class<?>> list, boolean interFace) {
        if (clazz == Object.class || clazz == null) {
            return;
        }
        //superClass和 interfaces合并
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            list.add(superClass);
            getSuperClass(superClass, list, interFace);
        }
        if (interFace) {
            for (Class<?> anInterface : clazz.getInterfaces()) {
                list.add(anInterface);
                getSuperClass(anInterface, list, interFace);
            }
        }

    }

}
