package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.returnType.InjectReturnType;
import org.objectweb.asm.MethodVisitor;

public interface IInjectResult {
    int injectResult(MethodVisitor mv, int resultIndex, String currentClassName);
    InjectReturnType getReturnType(MethodVisitor mv, String currentClassName);
    boolean needFrame();
}
