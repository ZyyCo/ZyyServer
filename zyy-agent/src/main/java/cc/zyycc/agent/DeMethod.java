package cc.zyycc.agent;

import cc.zyycc.agent.enhancer.code.IVisitCode;
import org.objectweb.asm.MethodVisitor;

public class DeMethod {
    private final String targetMethodName;
    private final IVisitCode iVisitCode;
    private  String descriptor = null;


    public DeMethod(String targetMethodName, IVisitCode iVisitCode){
        this.targetMethodName = targetMethodName;
        this.iVisitCode = iVisitCode;
    }

    public DeMethod(String targetMethodName, String descriptor, IVisitCode iVisitCode){
        this.targetMethodName = targetMethodName;
        this.descriptor = descriptor;
        this.iVisitCode = iVisitCode;
    }


    public String getTargetMethodName() {
        return targetMethodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public IVisitCode getiVisitCode() {
        return iVisitCode;
    }
}
