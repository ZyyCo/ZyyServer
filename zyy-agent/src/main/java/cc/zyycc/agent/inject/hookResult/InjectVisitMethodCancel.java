package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.ConditionReturn;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.ReturnType;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectVisitMethodCancel extends InjectVisitMethodHookBase {
    private final ConditionReturn conditionReturn;

    public InjectVisitMethodCancel(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, ConditionReturn conditionReturn) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
        this.conditionReturn = conditionReturn;
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        return new ReturnType(conditionReturn.getDesc());
    }

    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
        Label skipReturn = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, skipReturn);

        mv.visitInsn(conditionReturn.getIconst());
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitLabel(skipReturn);

        return resultIndex;
    }

    @Override
    public boolean needFrame() {
        return true;
    }
}
