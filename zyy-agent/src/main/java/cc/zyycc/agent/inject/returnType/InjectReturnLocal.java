package cc.zyycc.agent.inject.returnType;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectReturnLocal extends InjectReturnType {


    private final int index;

    public InjectReturnLocal(int returnLocalindex, String desc) {
        super("", desc);
        this.index = returnLocalindex;
    }

    @Override
    public int go(String className, MethodVisitor mv, int currentLocal) {
        scanLocalVariable(mv, index);
        return currentLocal;
    }


}
