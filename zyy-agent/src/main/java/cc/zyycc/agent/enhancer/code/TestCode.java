package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class TestCode implements IVisitCode {
    public static int line = 0;

    @Override
    public Consumer<MethodVisitor> code() {
        return null;
    }

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!(
                name.equals("add"))
        ) {
            return null;
        }
//
        return mv -> {

            mv.visitVarInsn(ALOAD, 1); // 加载 cl
            Label labelNotNull = new Label();
            mv.visitJumpInsn(IFNONNULL, labelNotNull);
            mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/bridge/BridgeHolder", "getSpiLoader", "()Lcc/zyycc/common/bridge/PluginLoaderBridge;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "cc/zyycc/common/bridge/ForgeSpiBridge", "getClassLoader", "()Ljava/lang/ClassLoader;", false);

            mv.visitVarInsn(ASTORE, 1);
            mv.visitLabel(labelNotNull);

            // if (cl instanceof TransformerClassLoader)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "cpw/mods/modlauncher/TransformationServicesHandler$TransformerClassLoader");
            Label labelSkip = new Label();
            mv.visitJumpInsn(IFEQ, labelSkip);

            // cl = BridgeHolder.INSTANCE.getClassLoader()

            mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/bridge/BridgeHolder", "getSpiLoader", "()Lcc/zyycc/common/bridge/PluginLoaderBridge;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "cc/zyycc/common/bridge/ForgeSpiBridge", "getClassLoader", "()Ljava/lang/ClassLoader;", false);

            mv.visitVarInsn(ASTORE, 1);
            mv.visitLabel(labelSkip);





            System.out.println("owner:" + owner + "  name:" + name + "   descriptor:" + descriptor);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(name + " 即将被调用：ModDirTransformerDiscoverer.getExtraLocators()" + line);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            line++;


            // 加载 this
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            // 调用 Hook.saveDiscoverer(Object)
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cc/zyycc/forge/debug/AgentHook",
                    "saveDiscoverer",
                    "(Ljava/lang/Object;)V",
                    false
            );

        };

    }

    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        return null;
    }

}
