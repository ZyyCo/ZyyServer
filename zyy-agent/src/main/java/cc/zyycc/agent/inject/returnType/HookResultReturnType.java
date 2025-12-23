package cc.zyycc.agent.inject.returnType;

import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HookResultReturnType extends InjectReturnType {

    public HookResultReturnType(String resultPackage) {
        super("HookResult", "L" + resultPackage + "/HookResult;");
    }

    public int go(String className, MethodVisitor mv, int localIndex) {
        mv.visitVarInsn(Opcodes.ASTORE, localIndex);
        return localIndex;
    }


}
