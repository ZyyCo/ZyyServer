package cc.zyycc.agent.inject.returnType;

import org.objectweb.asm.MethodVisitor;

public class ReturnType extends InjectReturnType {

    public ReturnType(String returnDesc) {
        super("", returnDesc);
    }


    @Override
    public int go(String currentClassName, MethodVisitor mv, int localIndex) {
        return localIndex;
    }
}
