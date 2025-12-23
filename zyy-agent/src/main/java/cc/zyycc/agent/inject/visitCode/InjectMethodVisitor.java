package cc.zyycc.agent.enhancer;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class InjectMethodVisitor extends MethodVisitor {

    private final List<InjectVisitMethod> injectVisitMethods;

    public InjectMethodVisitor(MethodVisitor mv, List<InjectVisitMethod> injectVisitMethods) {
        super(Opcodes.ASM9, mv);
        this.injectVisitMethods = injectVisitMethods;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
            injectVisitMethod.scanField(name, descriptor);
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        for (InjectVisitMethod injectVisitMethod : injectVisitMethods) {
            if (injectVisitMethod.isTargetMethod(name, descriptor)) {
                injectVisitMethod.injectCode(injectVisitMethod.getInjectClassName());
            }
        }


//        if (descriptor.equals("Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I")) {
//            injectMethod[0].injectCode(this, className);
//        }


        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            for (InjectVisitMethod inject : injectVisitMethods) {
                if (inject.injectPosition == InjectVisitMethod.AFTER_SUPER) {
                    inject.injectCode(this, inject.getInjectClassName());
                }
            }
//            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
//            mv.visitTypeInsn(Opcodes.NEW, "com/mojang/brigadier/CommandDispatcher");
//            mv.visitInsn(Opcodes.DUP);
//            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/mojang/brigadier/CommandDispatcher", "<init>", "()V", false);
//            mv.visitFieldInsn(Opcodes.PUTFIELD, className, "field_197062_b", "Lcom/mojang/brigadier/CommandDispatcher;");


        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        for (InjectVisitMethod inject : injectVisitMethods) {
            if (inject.injectPosition == InjectVisitMethod.AFTER_RETURN) {
                inject.injectCode(mv, inject.getInjectClassName());
            }
        }
        super.visitEnd();
    }


}
