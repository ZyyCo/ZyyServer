package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.returnType.HookResultReturnType;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectVisitMethodHookResultCancel extends InjectVisitMethodHookBase {
    public InjectVisitMethodHookResultCancel(String resultPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget) {
        super(resultPackage, injectClassName, injectMethodName, varCapture, injectTarget);
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        return new HookResultReturnType(resultPackage);
    }
    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
        Label endIf1 = new Label();
        mv.visitVarInsn(Opcodes.ALOAD, resultIndex);

        mv.visitJumpInsn(Opcodes.IFNULL, endIf1);
        mv.visitVarInsn(Opcodes.ALOAD, resultIndex);

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                resultPackage + "/HookResult", "isCancelled", "()Z", false);

        mv.visitJumpInsn(Opcodes.IFEQ, endIf1);

        String returnDesc = getTargetMethodDesc().substring(getTargetMethodDesc().lastIndexOf(")") + 1);
        if (returnDesc.equals("V")) {
            mv.visitInsn(Opcodes.RETURN);
        } else if (returnDesc.startsWith("L") || returnDesc.startsWith("[")) {
            mv.visitVarInsn(Opcodes.ALOAD, resultIndex);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    resultPackage + "/HookResult", "getResult", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(Opcodes.CHECKCAST, returnDesc.substring(1, returnDesc.length() - 1)); // 去掉 L 和 ;
            mv.visitInsn(Opcodes.ARETURN);
        } else if (returnDesc.equals("I") || returnDesc.equals("Z")) {
            getResult(mv, resultIndex, "java/lang/Integer", "intValue", "()I", Opcodes.IRETURN);
        } else if (returnDesc.equals("J")) {  // long
            getResult(mv, resultIndex, "java/lang/Long", "longValue", "()J", Opcodes.LRETURN);
        } else if (returnDesc.equals("F")) {
            getResult(mv, resultIndex, "java/lang/Float", "floatValue", "()F", Opcodes.FRETURN);
        } else if (returnDesc.equals("D")) {
            getResult(mv, resultIndex, "java/lang/Double", "doubleValue", "()D", Opcodes.DRETURN);
        }
        mv.visitLabel(endIf1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        return resultIndex;
    }

    @Override
    public boolean needFrame() {
        return true;
    }
}
