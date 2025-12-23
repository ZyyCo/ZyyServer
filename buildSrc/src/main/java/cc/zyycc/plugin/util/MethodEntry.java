package cc.zyycc.plugin.util;

public class BKMethodData {
    public String methodName;
    public String methodDesc;
    public String className;
    public BKMethodData(String className, String methodName, String methodDesc) {
        this.className = className;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

}
