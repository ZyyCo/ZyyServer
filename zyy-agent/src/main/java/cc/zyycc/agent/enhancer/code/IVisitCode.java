package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public interface IVisitCode {
    Consumer<MethodVisitor> code();

    default Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        return mv -> mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }


    default Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor){
        return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor);
    }

    default Consumer<MethodVisitor> visitTypeInsn(int opcode, String type) {
        return mv -> mv.visitTypeInsn(opcode, type);
    }

    default Consumer<MethodVisitor> visitInsn(int opcode) {
        return mv -> mv.visitInsn(opcode);
    }
}
