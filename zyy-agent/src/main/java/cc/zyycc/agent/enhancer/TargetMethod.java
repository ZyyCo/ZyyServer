package cc.zyycc.agent.enhancer;



import cc.zyycc.agent.inject.InjectVisitMethod;

import java.util.Objects;
import java.util.function.Predicate;

public class TargetMethod {

    private final String methodName;
    private String descriptor;

    protected Predicate<String> method = name -> false;

    protected Predicate<String> desc = desc -> false;

    public TargetMethod(String targetMethodName) {
        this(targetMethodName, null);
    }

    public static TargetMethod empty() {
        return new TargetMethod("");
    }



    public TargetMethod(String targetMethodName, String targetDescriptor) {
        this.methodName = targetMethodName;
        if (targetMethodName.equals("*")) {
            method = name -> true;
        } else {
            if (targetMethodName.endsWith("*")) {
                String prefix = targetMethodName.substring(0, targetMethodName.length() - 1);
                method = method.or(name -> name.startsWith(prefix));
            } else {
                method = method.or(name -> name.equals(targetMethodName));
            }
        }
        if (targetDescriptor != null) {
            if (targetDescriptor.endsWith("*")) {
                String prefix = targetDescriptor.substring(0, targetDescriptor.length() - 1);
                desc = desc.or(name -> targetDescriptor.startsWith(prefix));
            } else {
                desc = desc.or(name -> name.equals(targetDescriptor));
            }
        } else {
            desc = desc.or(name -> true);
        }

    }



    public boolean isTargetMethod(String methodName, String descriptor) {
        if(method.test(methodName) && desc.test(descriptor)){
            if (this.descriptor == null) {
                this.descriptor = descriptor;
            }
            return true;
        }
        return false;
    }

    public String getDesc() {
        return descriptor;
    }

    public void setDesc(String descriptor) {
        this.descriptor = descriptor;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TargetMethod that = (TargetMethod) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, descriptor);
    }
}
