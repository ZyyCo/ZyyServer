package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;

public class InjectVisitMethodHook extends InjectVisitMethodHookBase {
    public InjectVisitMethodHook(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        return InjectReturnType.empty();
    }

    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
        return resultIndex;
    }
}
