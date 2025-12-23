package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalVarCapture implements VarCapture, IReturnCapture {
    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    private int[] variableCaptureInt;
    private String[] variableCaptureStr;

    public LocalVarCapture(String... variableCapture) {
        this.variableCaptureStr = variableCapture;
    }

    public LocalVarCapture(int... variableCapture) {
        this.variableCaptureInt = variableCapture;
    }

    @Override
    public void scanField(String name, String descriptor) {
        if (variableCaptureStr != null) {
            for (String passParameter : variableCaptureStr) {
                if (name.equals(passParameter)) {
                    collectedFields.put(name, descriptor);
                }
            }
        } else if (variableCaptureInt != null) {
            for (int passParameter : variableCaptureInt) {
                if (passParameter == Integer.parseInt(name)) {
                    collectedFields.put(name, descriptor);
                }
            }
        }
        throw new RuntimeException("请传入int[]或者String[]");
    }

    @Override
    public void pushReplaceVar(MethodVisitor mv, int resultIndex, String desc) {
        if (variableCaptureStr != null) {
            desc = collectedFields.get(variableCaptureStr[0]);
        }

        if (desc.startsWith("L") || desc.startsWith("[")) {
            mv.visitVarInsn(Opcodes.ASTORE, variableCaptureInt[0]);
        } else if (desc.equals("I") || desc.equals("Z")) {
            mv.visitVarInsn(Opcodes.ISTORE, variableCaptureInt[0]);
        } else if (desc.equals("J")) {  // long
            mv.visitVarInsn(Opcodes.LSTORE, variableCaptureInt[0]);
        } else if (desc.equals("F")) {
            mv.visitVarInsn(Opcodes.FSTORE, variableCaptureInt[0]);
        } else if (desc.equals("D")) {
            mv.visitVarInsn(Opcodes.DSTORE, variableCaptureInt[0]);
        }

//
//
//        if (variableCaptureInt != null) {
//            mv.visitVarInsn(Opcodes.ASTORE, variableCaptureInt[0]);
//        }else if (variableCaptureStr != null) {
//            mv.visitVarInsn(Opcodes.ASTORE, resultIndex);
//        }
    }

    @Override
    public Map<String, String> getCollectedFields() {
        return collectedFields;
    }

    @Override
    public void load(MethodVisitor mv, String currentClassName) {
        for (Map.Entry<String, String> entry : collectedFields.entrySet()) {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    currentClassName,
                    entry.getKey(),
                    entry.getValue());
        }
    }

    @Override
    public String getDesc() {
        return String.join("", collectedFields.values());
    }
}
