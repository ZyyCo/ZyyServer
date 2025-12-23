package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.inject.GlobalVarCapture;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunctionBase;
import org.objectweb.asm.MethodVisitor;


public class NMSConstructor extends InjectInNewFunctionBase {

    public NMSConstructor() {
        super(null);
    }

    @Override
    public boolean needFrame() {
        return false;
    }

    @Override
    public int injectCode(MethodVisitor mv, String currentClassName) {
        return 0;
    }

    @Override
    public void scanField(String name, String descriptor) {

    }
}
