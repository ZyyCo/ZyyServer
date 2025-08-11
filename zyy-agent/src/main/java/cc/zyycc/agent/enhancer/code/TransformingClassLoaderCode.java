package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.function.Consumer;


public class TransformingClassLoaderCode implements IVisitCode {
    @Override
    public Consumer<MethodVisitor> code() {

        return null;
    }


    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        return mv -> {

            if (
                    opcode == Opcodes.INVOKESPECIAL &&
                    "java/lang/ClassLoader".equals(owner) &&
                    "<init>".equals(name) &&
                    "()V".equals(descriptor)) {


                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/BridgeHolder",
                        "getInstance",
                        "()Lcc/zyycc/common/bridge/PluginLoaderBridge;",
                        false);

                mv.visitFieldInsn(Opcodes.GETFIELD,
                        "cc/zyycc/common/bridge/PluginLoaderBridge",
                        "classLoader",
                        "Ljava/lang/ClassLoader;");
                // 调用 super(classLoader)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                        "java/lang/ClassLoader",
                        "<init>",
                        "(Ljava/lang/ClassLoader;)V",
                        false);

            } else {
                // 保留原始调用
                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }



        };
    }
}
