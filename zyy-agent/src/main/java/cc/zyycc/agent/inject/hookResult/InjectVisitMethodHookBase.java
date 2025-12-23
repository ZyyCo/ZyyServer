package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.inject.*;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;


public abstract class InjectVisitMethodHookBase extends InjectVisitMethod implements IInjectResult{
    public InjectVisitMethodHookBase(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget) {
        super(returnPackage, injectClassName, injectMethodName, injectTarget, varCapture);
    }

    @Override
    public int injectCode(MethodVisitor mv, String currentClassName, Map<Integer, String> methodArgDesc, int nextLocal) {
        StringBuilder sb = new StringBuilder("(");
        MethodParaIndexCapture methodParam = varCapture.getMethodParam();
        if (methodParam == null && varCapture.getCaptures().isEmpty()) {
            //压栈
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            sb.append(methodArgDesc.get(0)); // 第一个参数 this
        } else {
            //压入的方法参数
            if (methodParam != null) {
                String load = methodParam.load(mv, methodArgDesc);
                sb.append(load);
            }
        }

        for (VarCapture capture : varCapture.getCaptures()) {
            capture.load(mv, currentClassName);
            sb.append(capture.getDesc());
        }

        this.returnType = getReturnType(mv, currentClassName);

        sb.append(")").append(returnType.getDesc());


        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                getInjectClassName(),
                getInjectMethodName(), sb.toString(), false);

        int resultIndex = returnType.go(currentClassName, mv, nextLocal);

        return injectResult(mv, resultIndex, currentClassName);
    }

    //
//    public abstract int injectResult(MethodVisitor mv, int resultIndex, String currentClassName);
//
//    public abstract InjectReturnType getReturnType(MethodVisitor mv, String currentClassName);


    void getResult(MethodVisitor mv, int resultIndex, String owner, String name, String desc, int opcode) {
        mv.visitVarInsn(Opcodes.ALOAD, resultIndex);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                resultPackage + "/HookResult", "getResult", "()Ljava/lang/Object;", false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc, false);
        mv.visitInsn(opcode);
    }
}
