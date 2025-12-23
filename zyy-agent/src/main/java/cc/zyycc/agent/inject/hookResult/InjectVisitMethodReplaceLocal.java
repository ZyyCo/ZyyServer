package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.ConditionReturn;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.ReturnType;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectVisitMethodReplace extends InjectVisitMethodHookBase {
    private final ConditionReturn conditionReturn;

    public InjectVisitMethodReplace(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, ConditionReturn conditionReturn) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
        this.conditionReturn = conditionReturn;
    }

    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {


        mv.visitInsn(conditionReturn.getIconst());


        return resultIndex;
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        return new ReturnType(conditionReturn.getDesc());
    }

    @Override
    public boolean needFrame() {
        return false;
    }
}
