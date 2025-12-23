package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.GlobalVarCapture;
import cc.zyycc.agent.inject.InjectInfo;
import cc.zyycc.agent.inject.method.InjectMethod;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public abstract class InjectInNewFunctionBase extends InjectMethod {

    protected final GlobalVarCapture globalVarCapture;

    public InjectInNewFunctionBase(GlobalVarCapture globalVarCapture) {//visitCode
        this("", "", "", globalVarCapture);
    }

    public InjectInNewFunctionBase(String resultPackage, String injectClassName, String injectMethodName, GlobalVarCapture globalVarCapture) {
        super(resultPackage, injectClassName, injectMethodName);
        this.globalVarCapture = globalVarCapture;
    }

    public InjectInNewFunctionBase(String resultPackage, String injectClassName, String injectMethodName, GlobalVarCapture globalVarCapture, InjectReturnType returnType) {
        super(resultPackage, injectClassName, injectMethodName, returnType);
        this.globalVarCapture = globalVarCapture;
    }

    public abstract InjectInfo injectCode(MethodVisitor mv, InjectInfo injectInfo);

    public void scanField(String name, String descriptor) {
        if (globalVarCapture != null) {
            globalVarCapture.scanField(name, descriptor);
        }
    }

    public void injectPush(MethodVisitor mv, String className, String descriptor, int index) {
    }


}
