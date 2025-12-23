package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.enhancer.ClassEnhancer;
import cc.zyycc.agent.enhancer.SimpleVisitMethodEnhancer;
import cc.zyycc.agent.inject.IInjectMode;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public abstract class InjectVisitCode extends IInjectMode {

    public Consumer<MethodVisitor> visitCode() {
        return MethodVisitor::visitCode;
    }


    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }


    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor, MyMethodVisitor context) {
        return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor);
    }

    public Consumer<MethodVisitor> visitTypeInsn(int opcode, final String type) {
        return mv -> mv.visitTypeInsn(opcode, type);
    }

    public Consumer<MethodVisitor> visitInsn(int opcode) {
        return mv -> mv.visitInsn(opcode);
    }

    public Consumer<MethodVisitor> visitVarInsn(final int opcode, final int varIndex) {
        return mv -> mv.visitVarInsn(opcode, varIndex);
    }


}
