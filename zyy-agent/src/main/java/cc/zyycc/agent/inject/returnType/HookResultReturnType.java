package cc.zyycc.agent.inject;

import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class HookResultReturnType extends InjectReturnType {

    public HookResultReturnType(String resultPackage) {
        super("HookResult", "L" + resultPackage + "/HookResult;");
    }

    public int go(String className, MethodVisitor mv) {
        int currentLocal = ((InjectMethodVisitor) mv).getCurrentLocal();
        mv.visitVarInsn(Opcodes.ASTORE, currentLocal);
        return currentLocal;
    }


}
