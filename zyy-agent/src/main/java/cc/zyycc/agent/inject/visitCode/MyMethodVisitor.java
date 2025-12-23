package cc.zyycc.agent.inject.visitCode;


import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import org.objectweb.asm.*;

import java.util.function.Consumer;


public class MyMethodVisitor extends MethodVisitor {
    private final InjectVisitCode injectVisitCode;
    private final String pluginName;
    private final ClassLoader classLoader;
    private final String methodName;
    private boolean enhancer;

    private int nextLocal = 0;
    private int maxStack;

    public MyMethodVisitor(int api, MethodVisitor methodVisitor, InjectVisitCode injectVisitCode, String pluginName, ClassLoader classLoader, String methodName) {
        super(api, methodVisitor);
        this.injectVisitCode = injectVisitCode;
        this.pluginName = pluginName;
        this.classLoader = classLoader;
        this.methodName = methodName;
    }


    @Override
    public void visitCode() {
        injectVisitCode.visitCode().accept(mv);
    }


    @Override
    public void visitTypeInsn(int opcode, String type) {
        injectVisitCode.visitTypeInsn(opcode, type).accept(mv);
    }

    @Override
    public void visitInsn(int opcode) {
        Consumer<MethodVisitor> methodVisitorConsumer = injectVisitCode.visitInsn(opcode);
        methodVisitorConsumer.accept(mv);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Consumer<MethodVisitor> methodVisitorConsumer = injectVisitCode.visitMethodInsn(opcode, owner, name, descriptor, isInterface, this);
        methodVisitorConsumer.accept(mv);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        Consumer<MethodVisitor> methodVisitorConsumer = injectVisitCode.visitFieldInsn(opcode, owner, name, descriptor, this);
        methodVisitorConsumer.accept(mv);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + this.maxStack, maxLocals);
    }


    public void addMaxLocals(int maxLocals) {
        this.nextLocal = Math.max(this.nextLocal, maxLocals);
    }

    public void addMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    //    @Override
//    public void visitIincInsn(int var, int increment) {
//        nextLocal = Math.max(nextLocal, var + 1);
//        super.visitIincInsn(var, increment);
//    }
//    @Override
//    public void visitVarInsn(int opcode, int varIndex) {
//        int size = (opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE) ? 2 : 1;
//        nextLocal = Math.max(nextLocal, varIndex + size);
//        super.visitVarInsn(opcode, varIndex);
//    }
//
//    @Override
//    public void visitMaxs(int maxStack, int maxLocals) {
//        super.visitMaxs(maxStack, nextLocal + 20);
//    }


    public String getPluginName() {
        return pluginName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public boolean isEnhancer() {
        return enhancer;
    }
}
