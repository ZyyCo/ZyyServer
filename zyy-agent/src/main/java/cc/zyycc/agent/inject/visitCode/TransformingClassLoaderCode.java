package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.inject.visitCode.MyMethodVisitor;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;


public class TransformingClassLoaderCode extends InjectVisitCode {

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> {
            if (
                    opcode == Opcodes.INVOKESPECIAL &&
                    "java/lang/ClassLoader".equals(owner) &&
                    "<init>".equals(name) &&
                    "()V".equals(descriptor)) {


                mv.visitFieldInsn(GETSTATIC, "cc/zyycc/common/loader/MyLoader", "ZYY", "Lcc/zyycc/common/loader/MyLoader;");

                mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/loader/LoaderManager",
                        "getClassLoader",
                        "(Lcc/zyycc/common/loader/MyLoader;)Ljava/lang/ClassLoader;",
                        false);

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

    @Override
    public boolean needFrame() {
        return false;
    }
}
