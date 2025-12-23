package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.inject.method.*;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.scan.IScanVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class InjectVisitMethod extends InjectMethod {

    public static final int AFTER_START = 0;
    public static final int AFTER_RETURN = 1;
    private final InjectTarget injectTarget;

    protected VariableCapture varCapture;

    protected Predicate<String> desc = desc -> false;

    public int injectPosition = -1;

    private final InsnContext insnContext = new InsnContext();



    public InjectVisitMethod(String injectPackageName, String injectClassName, String injectMethodName, InjectTarget injectTarget, VariableCapture varCapture) {
        super(injectPackageName, injectClassName, injectMethodName, null);
        this.injectTarget = injectTarget;
        this.varCapture = varCapture;
    }


    public boolean hasStartInject() {
        return varCapture.returnCapture != null && varCapture.returnCapture.getInjectPoint() == AFTER_START;
    }

    public boolean hasReturnInject() {
        return varCapture.returnCapture != null && varCapture.returnCapture.getInjectPoint() == AFTER_RETURN;
    }


    public int getPoint() {
        return injectTarget.getPoint();
    }

    public boolean isTargetCode(int opcode) {
        if(injectTarget instanceof InjectVisitInsnTarget){
            return injectTarget.matches(insnContext.setCode(opcode));
        }
        return false;
    }


    public boolean isTargetMethod(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (injectTarget instanceof InjectMethodTarget) {
            return injectTarget.matches(insnContext.setMethod(opcode, owner, name, descriptor, isInterface));
        }
        return false;
    }

    public boolean isTargetField(int opcode, String owner, String name, String descriptor) {
        if (injectTarget instanceof InjectFieldTarget) {
            return injectTarget.matches(insnContext.setField(opcode, owner, name, descriptor));
        }
        return false;
    }

    public void scanField(String name, String descriptor) {
        for (VarCapture capture : varCapture.getCaptures()) {
            capture.scanField(name, descriptor);
        }
    }

    public void scanLocalVariable(String name, String descriptor, int currentLocalIndex) {

    }


    public List<VarCapture> getVarCapture() {
        return varCapture.getCaptures();
    }

    public int injectCode(MethodVisitor mv, String currentClassName, Map<Integer, String> methodArgDesc, int nextLocal) {
        //压栈
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        StringBuilder sb = new StringBuilder("(");

        sb.append("L").append("java/lang/Object").append(";"); // 第一个参数 this

        //方法参数
        MethodParaIndexCapture methodParam = varCapture.getMethodParam();
        if (methodParam != null) {
            String load = methodParam.load(mv, methodArgDesc);
            sb.append(load);
        }

        for (VarCapture capture : varCapture.getCaptures()) {
            capture.load(mv, currentClassName);
            sb.append(capture.getDesc());
        }


        if (returnType == null) {
            sb.append(")V");
        } else {
            sb.append(")").append(returnType.getDesc());
        }


        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                getInjectClassName(),
                getInjectMethodName(), sb.toString(), false);
        int go = 1;
        if (returnType != null) {
            go = returnType.go(currentClassName, mv, nextLocal);
        }
        return go;
    }

    @Override
    public boolean needFrame() {
        return false;
    }
}
