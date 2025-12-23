package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldScanCapture implements VarCapture {
    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    private final String[] fieldNames;

    public FieldScanCapture(String... fieldName) {
        this.fieldNames = fieldName;
    }


    @Override
    public void scanField(String name, String descriptor) {
        for (String fieldName : fieldNames) {
            if (name.equals(fieldName)) {
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
            // ALOAD 0 (this)
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    currentClassName,
                    entry.getKey(),
                    entry.getValue()
            );
        }


    }

    @Override
    public String getDesc() {
        return String.join("", collectedFields.values());
    }
}
