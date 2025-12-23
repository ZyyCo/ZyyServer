package cc.zyycc.agent.inject.returnType;

import cc.zyycc.agent.inject.ConditionReturn;
import org.objectweb.asm.MethodVisitor;

public class BooleanReturnType extends InjectReturnType{

    public BooleanReturnType(String returnDesc) {
        super("", returnDesc);
    }



    @Override
    public int go(String currentClassName, MethodVisitor mv, int localIndex) {

        return localIndex;
    }
}
