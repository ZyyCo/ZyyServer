package cc.zyycc.agent.inject.visitCode;


import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.enhancer.TargetMethod;
import cc.zyycc.agent.inject.GlobalVarCapture;
import cc.zyycc.agent.inject.InjectVisitMethod;
import cc.zyycc.agent.inject.method.InjectPoint;
import cc.zyycc.agent.inject.method.InsnContext;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectMethodVisitor extends MethodVisitor {

    private final List<InjectVisitMethod> injectVisitMethods;

    private final String currentClassName;

    private int nextLocal = 0;

    private final Map<Integer, String> methodArgDesc = new ConcurrentHashMap<>();

    public InjectMethodVisitor(MethodVisitor mv, String currentClassName, int access, String desc, Map<String, String> allInjectVisitMethods, List<InjectVisitMethod> injectVisitMethods) {
        super(Opcodes.ASM9, mv);
        this.injectVisitMethods = injectVisitMethods;
        this.currentClassName = currentClassName;
        this.nextLocal = calcMethodLocal(access, desc);
        injectVisitMethods.forEach(injectVisitMethod -> {
            List<VarCapture> varCapture = injectVisitMethod.getVarCapture();
            if (varCapture instanceof GlobalVarCapture) {
                GlobalVarCapture globalVarCapture = (GlobalVarCapture) varCapture;
                List<String> variableCapture = globalVarCapture.getVariableCapture();
                for (String var : variableCapture) {
                    String desc1 = allInjectVisitMethods.get(var);
                    if (desc1 != null && !desc1.isEmpty()) {
                        ((GlobalVarCapture) varCapture).getCollectedFields().put(var, desc1);
                    }
                }
            }
        });
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
            injectVisitMethod.scanField(name, descriptor);
            injectVisitMethod.scanLocalVariable(name, descriptor, nextLocal);
            if (injectVisitMethod.isTargetField(opcode, owner, name, descriptor)) {
                injectVisitMethod.injectCode(this, currentClassName, methodArgDesc, nextLocal);
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
            injectVisitMethod.scanField(name, descriptor);
            if (injectVisitMethod.isTargetMethod(opcode, owner, name, descriptor, isInterface)) {
                injectVisitMethod.injectCode(this, currentClassName, methodArgDesc, nextLocal);
            }
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }


    @Override
    public void visitCode() {
        //方法开始
        for (InjectVisitMethod inject : injectVisitMethods) {
            if (inject.hasStartInject()) {
                inject.injectCode(this, currentClassName, methodArgDesc, nextLocal);
            }
        }
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {

        if (opcode == Opcodes.RETURN) {
            for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
                if (injectVisitMethod.hasReturnInject()) {
                    injectVisitMethod.injectCode(this, currentClassName, methodArgDesc, nextLocal);
                }
            }
        }


        if (opcode != Opcodes.RETURN) {
            for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
                if (injectVisitMethod.isTargetCode(opcode)) {
                    if(injectVisitMethod.getPoint() == InjectPoint.INVOKE_BEFORE){
                        injectVisitMethod.injectCode(this, currentClassName, methodArgDesc, nextLocal);
                        super.visitInsn(opcode);
                    }else {
                        super.visitInsn(opcode);
                        injectVisitMethod.injectCode(this, currentClassName, methodArgDesc, nextLocal);
                    }
                    return;
                }
            }
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        // 如果 type != null，说明是 catch 块，会把异常存在一个新的 slot 中
        // 你可以预估一下这个异常变量要用 slot
        // 最简单就是直接加一：
//        nextLocal = Math.max(nextLocal, nextLocal + 1);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        nextLocal = Math.max(nextLocal, var + 1);
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        int size = (opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE) ? 2 : 1;
        nextLocal = Math.max(nextLocal, varIndex + size);
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, nextLocal + 1);
    }

    @Override
    public void visitEnd() {
        for (InjectVisitMethod inject : injectVisitMethods) {
            if (inject.injectPosition == InjectVisitMethod.AFTER_RETURN) {
                inject.injectCode(this, inject.getInjectClassName(), methodArgDesc, nextLocal);
            }
        }
        super.visitEnd();
    }

    public int getCurrentLocal() {
        return nextLocal;
    }

    public void setNextLocal(int nextLocal) {
        this.nextLocal = nextLocal;
    }

    public int newLocal(Type type) {
        int index = nextLocal;
        nextLocal += (type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE) ? 2 : 1;
        return index;
    }

    public int calcMethodLocal(int access, String desc) {
        Type[] args = Type.getArgumentTypes(desc);
        // 非 static 从 1 开始（slot 0 = this）
        int nextLocal = ((access & Opcodes.ACC_STATIC) != 0) ? 0 : 1;
        if (nextLocal == 1) {//不是static
            methodArgDesc.put(0, Type.getObjectType(currentClassName).getDescriptor());
        }
        // 把所有参数大小算进去
        for (Type t : args) {
            methodArgDesc.put(nextLocal, t.getDescriptor());
            for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
                injectVisitMethod.scanLocalVariable("", t.getDescriptor(), nextLocal);
            }
            nextLocal += (t == Type.LONG_TYPE || t == Type.DOUBLE_TYPE) ? 2 : 1;

        }
        return nextLocal;
    }

}
