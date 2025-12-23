package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;

public class CustomMethodHook extends InjectVisitMethodHookBase {

    private final IInjectResult injectResult;

    public CustomMethodHook(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, IInjectResult injectResult) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
        this.injectResult = injectResult;
    }

    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
        return injectResult.injectResult(mv, resultIndex, currentClassName);
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        return injectResult.getReturnType(mv, currentClassName);
    }

    @Override
    public boolean needFrame() {
        return injectResult.needFrame();
    }
}
