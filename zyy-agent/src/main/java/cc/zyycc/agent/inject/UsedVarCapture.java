package cc.zyycc.agent.inject;

import cc.zyycc.agent.VarCapture;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsedVarCapture implements VarCapture {
    private final String name;
    private final String descriptor;
    private final Map<String, String> variableCapture = new ConcurrentHashMap<>();

    public UsedVarCapture (String name, String descriptor){
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public void scanField(String name, String descriptor) {

    }

    @Override
    public Map<String, String> getCollectedFields() {
        return variableCapture;
    }

    @Override
    public void load(MethodVisitor mv, String currentClassName) {

    }
    @Override
    public String getDesc() {
        return null;
    }
}
