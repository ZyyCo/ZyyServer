package cc.zyycc.agent.enhancer;


import cc.zyycc.agent.enhancer.code.IVisitCode;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;


public class MyMethodVisitor extends MethodVisitor {
    private final IVisitCode iVisitCode;

    public MyMethodVisitor(int api, MethodVisitor methodVisitor, IVisitCode iVisitCode) {
        super(api, methodVisitor);
        this.iVisitCode = iVisitCode;

    }



    @Override
    public void visitCode() {
        if (iVisitCode.code() != null) {
            iVisitCode.code().accept(mv);
        } else {
            super.visitCode();
        }
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        iVisitCode.visitTypeInsn(opcode, type).accept(mv);
    }

    @Override
    public void visitInsn(int opcode) {
        Consumer<MethodVisitor> methodVisitorConsumer = iVisitCode.visitInsn(opcode);
        methodVisitorConsumer.accept(mv);
    }



    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Consumer<MethodVisitor> methodVisitorConsumer = iVisitCode.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        methodVisitorConsumer.accept(mv);

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        Consumer<MethodVisitor> methodVisitorConsumer = iVisitCode.visitFieldInsn(opcode, owner, name, descriptor);
        methodVisitorConsumer.accept(mv);
    }


}
