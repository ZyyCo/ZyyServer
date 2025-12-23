package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.returnType.InjectReturnLocal;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectVisitMethodHookResultReplace extends InjectVisitMethodHookBase {


    private int[] returnLocals;
    private String replaceName;
    private final Map<Integer, String> replaceLocals = new ConcurrentHashMap<>();

    public InjectVisitMethodHookResultReplace(String injectPackageName, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, int... args) {
        super(injectPackageName, injectClassName, injectMethodName, varCapture, injectTarget);
        this.returnLocals = args;
    }

    public InjectVisitMethodHookResultReplace(String injectPackageName, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, String... args) {
        super(injectPackageName, injectClassName, injectMethodName, varCapture, injectTarget);
        this.replaceName = args[0];
    }


    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        for (Map.Entry<Integer, String> entry : replaceLocals.entrySet()) {
            return new InjectReturnLocal(entry.getKey(), entry.getValue());
        }
        return new InjectReturnType("", "", 0);
    }

    @Override
    public void scanLocalVariable(String name, String descriptor, int currentLocalIndex) {
        if (replaceName != null && replaceName.equals(name)) {
            replaceLocals.put(currentLocalIndex, descriptor);
        }
        for (int returnLocal : returnLocals) {
            if (currentLocalIndex == returnLocal) {
                replaceLocals.put(returnLocal, descriptor);
            }
        }
    }


    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
//        mv.visitVarInsn(Opcodes.ALOAD, 1);
//        InjectReturnType returnType = new InjectReturnType(replaceName,
//                replaceDesc, 1);

        //int go = returnType.go(currentClassName, mv);

        return resultIndex;
    }
}
