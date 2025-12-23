package cc.zyycc.plugin.util;

import java.util.Objects;

public class MethodEntry {
    public String confusedMethod;
    public String methodDesc;
    public String className;
    public MethodEntry(String className, String confusedMethod, String methodDesc) {
        this.className = className;
        this.confusedMethod = confusedMethod;
        this.methodDesc = methodDesc;
    }


    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodEntry that = (MethodEntry) o;
        return Objects.equals(confusedMethod, that.confusedMethod) && Objects.equals(methodDesc, that.methodDesc) && Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confusedMethod, methodDesc, className);
    }
}
