package cc.zyycc.agent.inject;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectReturnTypeArray extends InjectReturnType {
    private final int[] args;

    public InjectReturnTypeArray(int... args) {
        super("", "");
        this.args = args;
    }

    public int go(String currentClassName, MethodVisitor mv) {

        mv.visitVarInsn(Opcodes.ASTORE, this.getArgIndex());
        return this.getArgIndex();

    }


}
