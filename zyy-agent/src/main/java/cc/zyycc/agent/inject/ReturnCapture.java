package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReturnCapture implements VarCapture {
    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    private final int injectPoint;
    public List<String> variableCapture;

    public ReturnCapture(int injectPoint,String... variableCapture) {
        this.injectPoint = injectPoint;
        this.variableCapture = Arrays.asList(variableCapture);
    }

    @Override
    public void scanField(String name, String descriptor) {
        for (String passParameter : variableCapture) {
            if (name.equals(passParameter)) {
                collectedFields.put(name, descriptor);
            }
        }
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

    public int getInjectPoint() {
        return injectPoint;
    }

    @Override
    public String getDesc() {
        return String.join("", collectedFields.values());
    }
}
