package cc.zyycc.agent.enhancer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectMethod {
    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    protected final String[] passParameters;
    private final String injectMethodName;
    private final String injectClassName;
    private final InjectReturnType returnType;


    public InjectMethod(String injectClassName, String injectMethodName, String... passParameters) {
        this(injectClassName, injectMethodName, null, passParameters);
    }

    public InjectMethod(String injectClassName, String injectMethodName, InjectReturnType returnType, String... passParameters) {
        this.passParameters = passParameters;
        this.injectMethodName = injectMethodName;
        this.returnType = returnType;
        this.injectClassName = injectClassName;
    }
//
//    public InjectMethod(String injectClassName, String injectMethodName, InjectReturnType returnType, InjectVisitMethod[] injectMethodVisitor) {
//        this.injectMethodVisitor = injectMethodVisitor;
//        List<String> list = new ArrayList<>();
//        for (InjectVisitMethod injectVisitMethod : injectMethodVisitor) {
//            Collections.addAll(list, injectVisitMethod.injectFields);
//        }
//        this.passParameters = list.toArray(new String[0]);
//        this.injectMethodName = injectMethodName;
//        this.returnType = returnType;
//        this.injectClassName = injectClassName;
//
//    }


    public void scanField(String name, String descriptor) {
        for (String passParameter : passParameters) {
            if (name.equals(passParameter)) {//"field_197062_b"
                collectedFields.put(name, descriptor);
            }
        }
    }

    public void injectCode(String className) {
    }


    public void injectCode(MethodVisitor mv, String className) {

        //压栈
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        StringBuilder sb = new StringBuilder("(");
        sb.append("L").append(className).append(";"); // 第一个参数 this

        for (Map.Entry<String, String> entry : collectedFields.entrySet()) {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this.field_197062_b
            mv.visitFieldInsn(Opcodes.GETFIELD, className, entry.getKey(), entry.getValue());
            sb.append(entry.getValue());
        }
        if (returnType == null) {
            sb.append(")V");
        } else {
            sb.append(")").append(returnType.getDesc());
        }


        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                injectClassName,
                injectMethodName, sb.toString(), false);

        if (returnType != null) {
            returnType.go(className, mv);
        }
    }

    public String getInjectMethodName() {
        return injectMethodName;
    }

    public String getInjectClassName() {
        return injectClassName;
    }

    public int maxStack() {
        return collectedFields.size() + 2;
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


}
