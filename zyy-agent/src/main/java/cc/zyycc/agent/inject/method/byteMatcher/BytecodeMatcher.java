package cc.zyycc.agent.inject.method.byteMatcher;

import org.objectweb.asm.MethodVisitor;

public interface BytecodeMatcher {

    MethodVisitor match(MethodVisitor mv);


}
