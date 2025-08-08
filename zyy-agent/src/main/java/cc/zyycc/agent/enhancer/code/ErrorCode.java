package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ErrorCode implements IVisitCode{

    @Override
    public Consumer<MethodVisitor> code() {

        return mv -> {
            mv.visitCode();




            mv.visitVarInsn(ALOAD, 1); // 加载 cl
            Label labelNotNull = new Label();
            mv.visitJumpInsn(IFNONNULL, labelNotNull);
            mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/bridge/BridgeHolder",
                    "getInstance",
                    "()Lcc/zyycc/common/bridge/PluginLoaderBridge;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "cc/zyycc/common/bridge/PluginLoaderBridge", "getClassLoader", "()Ljava/lang/ClassLoader;", false);

            mv.visitVarInsn(ASTORE, 1);
            mv.visitLabel(labelNotNull);

            // if (cl instanceof TransformerClassLoader)
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, "cpw/mods/modlauncher/TransformationServicesHandler$TransformerClassLoader");
            Label labelSkip = new Label();
            mv.visitJumpInsn(IFEQ, labelSkip);

            // cl = BridgeHolder.INSTANCE.getClassLoader()

            mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/bridge/BridgeHolder",
                    "getInstance", "()Lcc/zyycc/common/bridge/PluginLoaderBridge;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "cc/zyycc/common/bridge/PluginLoaderBridge",
                    "getClassLoader", "()Ljava/lang/ClassLoader;", false);

            mv.visitVarInsn(ASTORE, 1);
            mv.visitLabel(labelSkip);
            //
//            mv.visitVarInsn(Opcodes.ALOAD, 0);
//            // 调用 Hook.saveDiscoverer(Object)
//            mv.visitMethodInsn(
//                    Opcodes.INVOKESTATIC,
//                    "cc/zyycc/forge/debug/AgentHook",
//                    "error",
//                    "(Ljava/lang/Object;)V",
//                    false
//            );




//
//            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn("🧨 cl 最终使用的是 = ");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
//
//            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);

            //字节码反射
            // 准备 String[] 参数：new String[0]
//            mv.visitInsn(ICONST_0);
//            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
//
//            // 调用 Entry.start(String[]) 方法
//            mv.visitMethodInsn(INVOKESTATIC,
//                    "cc/zyycc/agent/Entry", // internal name
//                    "start",                // 方法名
//                    "([Ljava/lang/String;)V", // 方法签名
//                    false);                 // isInterface = false

        };
    }


}
