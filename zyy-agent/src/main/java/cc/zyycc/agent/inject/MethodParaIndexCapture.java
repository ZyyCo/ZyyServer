package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public class MethodIndexCapture implements VarCapture {
    @Override
    public void scanField(String name, String descriptor) {

    }

    @Override
    public Map<String, String> getCollectedFields() {
        return null;
    }

    @Override
    public void load(MethodVisitor mv, String currentClassName) {

    }

    @Override
    public int maxStack() {
        return 0;
    }

    @Override
    public String getDesc() {
        return null;
    }
}
