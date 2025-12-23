package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;


public class LocalVarIndexCapture implements VarCapture {
    private final int index;
    private final String desc;

    public LocalVarIndexCapture(int index,String desc) {
        this.index = index;
        this.desc = desc;
    }


    @Override
    public void scanField(String name, String descriptor) {

    }

    @Override
    public Map<String, String> getCollectedFields() {
        return new HashMap<>();
    }

    @Override
    public void load(MethodVisitor mv, String currentClassName) {
        mv.visitVarInsn(Opcodes.ALOAD, getIndex());
    }

    public String getDesc() {
        return desc;
    }

    public int getIndex() {
        return index;
    }
}
