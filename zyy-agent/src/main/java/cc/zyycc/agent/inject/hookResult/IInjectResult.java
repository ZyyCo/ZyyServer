package cc.zyycc.agent.inject.hookResult;

import org.objectweb.asm.MethodVisitor;

public interface CustomInjectResult {
    int injectResult(MethodVisitor mv, int resultIndex, String currentClassName);
}
