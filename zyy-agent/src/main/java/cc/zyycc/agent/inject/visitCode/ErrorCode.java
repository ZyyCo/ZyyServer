package cc.zyycc.agent.inject.visitCode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ErrorCode extends InjectVisitCode {
    public ErrorCode() {
    }


    @Override
    public Consumer<MethodVisitor> visitCode() {
        return mv -> {
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1); // 加载 cl
            Label labelNotNull = new Label();
            mv.visitJumpInsn(IFNONNULL, labelNotNull);


            mv.visitFieldInsn(GETSTATIC, "cc/zyycc/common/loader/MyLoader", "ZYY", "Lcc/zyycc/common/loader/MyLoader;");

            mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/loader/LoaderManager",
                    "getClassLoader",
                    "(Lcc/zyycc/common/loader/MyLoader;)Ljava/lang/ClassLoader;",
                    false);
            mv.visitVarInsn(ASTORE, 1);// 保存返回的 ClassLoader
            mv.visitLabel(labelNotNull);

            // if (cl instanceof TransformerClassLoader)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "cpw/mods/modlauncher/TransformationServicesHandler$TransformerClassLoader");
            Label labelSkip = new Label();
            mv.visitJumpInsn(IFEQ, labelSkip);

            // cl = BridgeHolder.INSTANCE.getClassLoader()

            mv.visitFieldInsn(GETSTATIC,
                    "cc/zyycc/common/loader/MyLoader",
                    "ZYY",
                    "Lcc/zyycc/common/loader/MyLoader;");

            mv.visitMethodInsn(INVOKESTATIC,
                    "cc/zyycc/common/loader/LoaderManager",
                    "getClassLoader",
                    "(Lcc/zyycc/common/loader/MyLoader;)Ljava/lang/ClassLoader;",
                    false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitLabel(labelSkip);
        };


    }

    @Override
    public boolean needFrame() {
        return false;
    }
}
