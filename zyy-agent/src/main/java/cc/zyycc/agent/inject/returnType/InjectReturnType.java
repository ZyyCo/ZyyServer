package cc.zyycc.agent.inject.returnType;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class InjectReturnType {
    private final String methodName;
    protected String desc;
    private final int argIndex;
    private final boolean isObject;
    private boolean empty;

    public InjectReturnType(String methodName, String desc) {
        this(methodName, desc, -1);
    }

    public static InjectReturnType empty() {
        InjectReturnType v = new InjectReturnType("", "V");
        v.empty = true;
        return v;
    }

    public InjectReturnType(String methodName, String desc, int argIndex) {
        this.isObject = desc.startsWith("L");
        this.methodName = methodName;
        this.desc = desc;
        this.argIndex = argIndex;//局部变量位置
    }


    public String getMethodName() {
        return methodName;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isModifyArg() {
        return argIndex > -1;
    }

    public int getArgIndex() {
        return argIndex;
    }

    public int go(String currentClassName, MethodVisitor mv, int localIndex) {
        if (empty) {
            return localIndex;
        }
        if (this.isModifyArg()) {
            if (isObject) {
                mv.visitVarInsn(Opcodes.ASTORE, this.getArgIndex());
            } else {
                mv.visitVarInsn(Opcodes.ISTORE, argIndex);
            }
            return this.getArgIndex();

        } else {
            // 调用完后重新压 this（因为前面的已被消费）
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            //调整栈顺序，让栈顶变成 [this, result]
            mv.visitInsn(Opcodes.SWAP);

            mv.visitFieldInsn(Opcodes.PUTFIELD,
                    currentClassName,
                    this.getMethodName(),
                    this.getDesc());
            return this.getArgIndex();
        }
//        return this.isModifyArg() ? this.getArgIndex() : newLocal(Type.getType(this.getDesc()), mv);
    }

    //自动计算索引
    public int newLocal(Type type, MethodVisitor mv) {
        return ((LocalVariablesSorter) mv).newLocal(type);
    }


    public void scanLocalVariable(MethodVisitor mv, int index) {
        if (desc.equals("V")) {
            return;
        } else if (desc.startsWith("L") || desc.startsWith("[")) {
            mv.visitVarInsn(Opcodes.ASTORE, index);
        } else if (desc.equals("I") || desc.equals("Z")) {
            mv.visitVarInsn(Opcodes.ISTORE, index);
        } else if (desc.equals("J")) {  // long
            mv.visitVarInsn(Opcodes.LSTORE, index);
        } else if (desc.equals("F")) {
            mv.visitVarInsn(Opcodes.FSTORE, index);
        } else if (desc.equals("D")) {
            mv.visitVarInsn(Opcodes.DSTORE, index);
        }
    }
}
