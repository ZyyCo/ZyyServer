package cc.zyycc.common.util;

import cc.zyycc.common.cache.CacheMapperData;
import cc.zyycc.common.cache.StrMappingCache;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class CacheUtil {

    public static CacheMapperData parseFieldCache(StrMappingCache cache, Class<?> clazz, String name) {
        String search = clazz.getName().replace(".", "/") + " " + name;
        String data = cache.getSuccessCache(search);
        if (data != null) {
            String className = data.substring(0, data.indexOf(" "));
            String fieldName = data.substring(data.lastIndexOf(" ") + 1);
            return new CacheMapperData(className, fieldName);
        }
        if (cache.hasFailCache(search)) {
            return CacheMapperData.error(clazz.getName().replace(".", "/"), name);
        }
        return null;
    }


    public static CacheMapperData parseMethodCache(StrMappingCache cache, Class<?> clazz, String name, String bkDesc) {
        String search = clazz.getName().replace(".", "/") + "/" + name + " " + bkDesc;
        String data = cache.getSuccessCache(search);
        if (data != null) {
            String replace = data.substring(0, data.indexOf(" "));
            String className = replace.substring(0, replace.lastIndexOf("/"));
            String methodName = replace.substring(replace.lastIndexOf("/") + 1);
            String desc = data.substring(data.indexOf(" ") + 1, data.indexOf(")") + 1) + "V";
            return new CacheMapperData(className, methodName, desc);
        }
        if (cache.hasFailCache(search)) {
            return CacheMapperData.error(clazz.getName().replace(".", "/"), name, bkDesc);
        }
        return null;
    }


    public static Class<?>[] getParams(ClassLoader loader, String desc) throws ClassNotFoundException {
        String params = desc.substring(desc.indexOf('(') + 1, desc.indexOf(')'));
        Type[] argumentTypes = Type.getArgumentTypes("(" + params + ")V");

        return convert(loader, argumentTypes);
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

    public static Type[] paramsToTypes(Class<?>[] params) {
        return Arrays.stream(params)
                .map(Type::getType)
                .toArray(Type[]::new);
    }
}
