package cc.zyycc.common.mapper.method;

import cc.zyycc.common.mapper.MappingEntry;

import java.util.Objects;

public class MethodMappingEntry extends MappingEntry {

    private String methodName;
    private String params;
    private String methodDesc;
    private String returnType;


    private MethodMappingEntry(String className, String methodName, String params, String returnType) {
        this.className = className.replace(".", "/");
        this.methodName = methodName;
        this.params = params;
        this.returnType = returnType;
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

    public static MethodMappingEntry createClassName(String className) {
        return new MethodMappingEntry(className, null, null, null);
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


    public String generateMethod() {
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
}
