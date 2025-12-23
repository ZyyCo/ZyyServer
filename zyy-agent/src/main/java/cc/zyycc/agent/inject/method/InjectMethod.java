package cc.zyycc.agent.inject.method;

import cc.zyycc.agent.inject.*;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class InjectMethod extends IInjectMode {
    private final String injectMethodName;
    private final String injectClassName;
    protected InjectReturnType returnType;
    protected final String resultPackage;
    protected String targetMethodDesc;

    public InjectMethod(String resultPackage, String injectClassName, String injectMethodName) {
        this(resultPackage, injectClassName, injectMethodName, null);
    }

    public InjectMethod(String resultPackage, String injectClassName, String injectMethodName, InjectReturnType returnType) {
        this.resultPackage = resultPackage;
        this.injectMethodName = injectMethodName;
        this.returnType = returnType;
        this.injectClassName = injectClassName;
    }

//    public VariableCapture getVariableCapture() {
//        return variableCapture;
//    }


    public abstract void scanField(String name, String descriptor);


    public String getInjectMethodName() {
        return injectMethodName;
    }

    public String getInjectClassName() {
        return injectClassName;
    }

    public void injectModifyArg(MethodVisitor mv, String className, int paramIndex) {
        // 1. 加载 this
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        // 2. 加载目标参数
        // 注意：索引 0 是 this，所以下一个是 paramIndex
        mv.visitVarInsn(Opcodes.ALOAD, paramIndex);

        // 3. 调用静态修改方法
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                injectClassName,
                injectMethodName,
                "(L" + className + ";Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        // 4. 覆盖回原参数槽位
        mv.visitVarInsn(Opcodes.ASTORE, paramIndex);
    }

    public InjectReturnType getReturnType() {
        return returnType;
    }

    public void setTargetMethodDesc(String targetMethodDesc) {
        this.targetMethodDesc = targetMethodDesc;
    }

    public String getTargetMethodDesc() {
        return targetMethodDesc;
    }


}
