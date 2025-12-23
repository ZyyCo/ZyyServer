package cc.zyycc.common.util;

import cc.zyycc.common.mapper.method.MethodMappingEntry;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Map;


public class MapperUtil {

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


    /**
     * 不带括号
     *
     * @return params
     */
    public static String mapDesc(String desc, Map<String, String> mappings) {
        if (desc.length() < 3) {
            return desc;
        }

        int start = 0;
        while (true) {
            int lnetIndex = desc.indexOf("Lnet", start);
            if (lnetIndex == -1) break; // 没找到就结束
            // 找到后面的 ';'
            int semicolonIndex = desc.indexOf(';', lnetIndex);
            //提取
            String className = desc.substring(lnetIndex + 1, semicolonIndex);
            String fClass = mappings.get(className);
            if (fClass != null) {
                desc = desc.replace(className, fClass);
            }

            start = lnetIndex + 1;
        }
        return desc;
    }

    /**
     * 解析descriptor
     * contains returnType
     *
     * @return params
     */

    public static String methodDesc(String desc, Map<String, String> mappings) {
        if (desc == null) {
            return null;
        }
        if (desc.length() < 6) {
            return desc;
        }
        if (desc.charAt(0) != '(') {
            return desc;
        }

        String oldParams = desc.substring(desc.indexOf('(') + 1, desc.indexOf(')'));
        String newParams = "(" + mapDesc(oldParams, mappings) + ")";
        String oldReturnType = desc.substring(desc.lastIndexOf(')') + 1);
        String newReturnType = mapDesc(oldReturnType, mappings);
        return newParams + newReturnType;
    }


}
