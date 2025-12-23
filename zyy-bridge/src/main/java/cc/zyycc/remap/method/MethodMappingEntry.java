package cc.zyycc.remap.method;


import cc.zyycc.remap.BaseEntry;
import cc.zyycc.remap.MappingUtil;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodMappingEntry extends BaseEntry {
    private String methodName;
    private String params;
    private String methodDesc;
    private String returnType;

    private MethodMappingEntry(String className, String methodName, String params, String returnType) {
        super(className, null);
        this.methodName = methodName;
        this.params = params;
        this.returnType = returnType;
    }

    @Override
    public MethodMappingEntry recreate(String value) {
        // value: owner/methodName desc
        int space = value.indexOf(' ');
        String ownerAndMethod = value.substring(0, space);
        String desc = value.substring(space + 1);

        // 分离 className 与 methodName
        int last = ownerAndMethod.lastIndexOf('/');
        String className = ownerAndMethod.substring(0, last);
        String methodName = ownerAndMethod.substring(last + 1);

        return MethodMappingEntry.create(className, methodName, desc);
    }


//    public static MethodMappingEntry createMethodMappingEntry(String key, String desc) {
//        String className = key.substring(0, key.lastIndexOf("/"));
//        String methodName = key.substring(key.lastIndexOf("/") + 1);
//        String params = desc.substring(desc.lastIndexOf("(") + 1, desc.lastIndexOf(")"));
//        MethodMappingEntry entry = new MethodMappingEntry(className, methodName, params);
//        entry.returnType = desc.substring(desc.lastIndexOf(")") + 1);
//        entry.methodDesc = desc;
//        return entry;
//    }

    public static MethodMappingEntry createMethodMappingEntry(String className, String methodName, String params, String returnType, String methodDesc) {
        MethodMappingEntry entry = new MethodMappingEntry(className, methodName, params, returnType);
        entry.methodDesc = methodDesc;
        return entry;
    }

    public static MethodMappingEntry create(String className, String methodName, String methodDesc) {
        String params = methodDesc.substring(methodDesc.indexOf('(') + 1, methodDesc.indexOf(')'));
        String returnType = methodDesc.substring(methodDesc.lastIndexOf(")") + 1);
        MethodMappingEntry entry = new MethodMappingEntry(className, methodName, params, returnType);
        entry.methodDesc = methodDesc;
        return entry;
    }

    public static MethodMappingEntry createSearchEntry(String className, String methodName, String methodParams) {
        return new MethodMappingEntry(className, methodName, methodParams, "V");
    }

    public static MethodMappingEntry createFiled(String className, String methodName) {
        return new MethodMappingEntry(className, methodName, null, "V");
    }

    public static MethodMappingEntry createSearchEntry(String className, String methodName, String methodParams, String returnType) {
        return new MethodMappingEntry(className, methodName, methodParams, returnType);
    }


    public String getMethodName() {
        return methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public String getParams() {
        return params;
    }


    public String getReturnType() {
        return returnType;
    }


    public String generate() {
        String desc;
        if (this.methodDesc == null) {
            desc = "(" +
                    params +
                    ")" +
                    returnType;
        } else {
            desc = methodDesc;
        }
        return className.replace(".", "/") + "/" +
                methodName +
                " " +
                desc;
    }

    public MethodMappingEntry copyWithClassName(String className) {
        MethodMappingEntry entry = new MethodMappingEntry(className, methodName, params, this.returnType);
        entry.methodDesc = methodDesc;
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodMappingEntry that = (MethodMappingEntry) o;
        return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName) && Objects.equals(params, that.params);
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public int hashCode() {
        if (methodDesc == null && params == null) {
            return Objects.hash(className, methodName);
        }
        return Objects.hash(className, methodName, params);
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setClassName(String className) {
        this.className = className.replace(".", "/");
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method anewExecuteMethod(ClassLoader loader, Class<?>... params) {
        try {
            Class<?> target = Class.forName(className.replace("/", "."), false, loader);
            try {
                return target.getDeclaredMethod(methodName, params);
            } catch (NoSuchMethodException ignore) {
                return target.getMethod(methodName, params);
            }

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?>[] getNewParams(MethodMappingEntry oldEntry, Class<?>[] originParams, ClassLoader classLoader) {
        try {
            if (oldEntry.getParams().equals(this.params)) {
                return MappingUtil.getParams(classLoader, this.methodDesc);
            }
            return originParams;
        } catch (ClassNotFoundException ignored) {
        }
        return originParams;
    }


}
