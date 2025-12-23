package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.enhancer.MyMethodVisitor;
import cc.zyycc.agent.inject.IInjectMethod;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public interface IVisitCode extends IInjectMethod {
    default Consumer<MethodVisitor> code(){
        return MethodVisitor::visitCode;
    }


    default Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }


    default Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor,MyMethodVisitor context){
        return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor);
    }

    default Consumer<MethodVisitor> visitTypeInsn(int opcode, String type) {
        return mv -> mv.visitTypeInsn(opcode, type);
    }

    default Consumer<MethodVisitor> visitInsn(int opcode) {
        return mv -> mv.visitInsn(opcode);
    }
}
