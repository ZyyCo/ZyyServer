package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.inject.HookResultReturnType;
import cc.zyycc.agent.inject.InjectVisitMethod;
import cc.zyycc.agent.inject.MethodParaIndexCapture;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public abstract class InjectVisitMethodHookResult extends InjectVisitMethod {
    public InjectVisitMethodHookResult(String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget) {
        super(injectClassName, injectMethodName, injectTarget, varCapture);
    }

    public int injectCode(MethodVisitor mv, String currentClassName) {
        //压栈
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        StringBuilder sb = new StringBuilder("(");
        sb.append("L").append(currentClassName).append(";"); // 第一个参数 this

        //方法参数
        MethodParaIndexCapture methodParam = varCapture.getMethodParam();
        if (methodParam != null) {
            String load = methodParam.load(mv, getTargetMethodDesc());
            sb.append(load);
        }


        for (VarCapture capture : varCapture.getCaptures()) {
            capture.load(mv, currentClassName);
            sb.append(capture.getDesc());
        }


        HookResultReturnType returnType = new HookResultReturnType();

        sb.append(")").append(returnType.getDesc());

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                getInjectClassName(),
                getInjectMethodName(), sb.toString(), false);

        int resultIndex = returnType.go(currentClassName, mv);


        return injectResult(mv, resultIndex, currentClassName);
    }

    public abstract int injectResult(MethodVisitor mv, int resultIndex, String currentClassName);

    void getResult(MethodVisitor mv, int resultIndex, String owner, String name, String desc, int opcode) {
        mv.visitVarInsn(Opcodes.ALOAD, resultIndex);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "cc/zyycc/bk/asm/util/HookResult", "getResult", "()Ljava/lang/Object;", false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc, false);
        mv.visitInsn(opcode);
    }
}
