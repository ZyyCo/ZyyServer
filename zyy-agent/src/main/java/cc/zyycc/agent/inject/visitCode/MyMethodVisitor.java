package cc.zyycc.agent.enhancer;


import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import org.objectweb.asm.*;

import java.util.function.Consumer;


public class MyMethodVisitor extends MethodVisitor {
    private final InjectVisitCode iVisitCode;
    private final String pluginName;
    private final ClassLoader classLoader;
    private final String methodName;
    private boolean enhancer;

    public MyMethodVisitor(int api, MethodVisitor methodVisitor, InjectVisitCode iVisitCode, String pluginName, ClassLoader classLoader, String methodName) {
        super(api, methodVisitor);
        this.iVisitCode = iVisitCode;
        this.pluginName = pluginName;
        this.classLoader = classLoader;
        this.methodName = methodName;
//        this.enhancer = enhancer;
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
        Consumer<MethodVisitor> methodVisitorConsumer = iVisitCode.visitMethodInsn(opcode, owner, name, descriptor, isInterface, this);
        methodVisitorConsumer.accept(mv);

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        Consumer<MethodVisitor> methodVisitorConsumer = iVisitCode.visitFieldInsn(opcode, owner, name, descriptor, this);
        methodVisitorConsumer.accept(mv);
    }

    public String getPluginName() {
        return pluginName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public boolean isEnhancer() {
        return enhancer;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setEnhancer() {
        enhancer = true;
    }
}
