package cc.zyycc.agent;

import cc.zyycc.agent.inject.scan.IScanVar;
import cc.zyycc.agent.transformer.scan.IScan;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public interface VarCapture extends IScanVar {
//    void scanField(String name, String descriptor);

    Map<String, String> getCollectedFields();

    void load(MethodVisitor mv, String currentClassName);
    String getDesc();
}
