package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalVarCapture implements VarCapture,IReturnCapture {

    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    public List<String> variableCapture;

    public GlobalVarCapture(String... variableCapture) {
        this.variableCapture = Arrays.asList(variableCapture);
    }

    public Map<String, String> getCollectedFields() {
        return collectedFields;
    }

    @Override
    public String getDesc() {
        return String.join("", collectedFields.values());
    }

    public List<String> getVariableCapture() {
        return variableCapture;
    }

    public void scanField(String name, String descriptor) {
        for (String passParameter : variableCapture) {
            if (name.equals(passParameter)) {
                collectedFields.put(name, descriptor);
            }
        }
    }

    @Override
    public void pushReplaceVar(MethodVisitor mv, int resultIndex, String desc) {

    }


    @Override
    public void load(MethodVisitor mv, String currentClassName) {
        // 加载所有匹配的字段
        for (Map.Entry<String, String> entry : collectedFields.entrySet()) {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD,
                    currentClassName,
                    entry.getKey(),
                    entry.getValue());
        }
    }

}
