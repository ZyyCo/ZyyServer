package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class Test2 implements IVisitCode {
    public static int line = 0;
    int state = 0;
    boolean patching = false;

    @Override
    public Consumer<MethodVisitor> code() {
        return null;
    }

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        return mv -> {
            if (patching && state == 2 &&
                    opcode == INVOKESPECIAL &&
                    owner.equals("net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader") &&
                    name.equals("<init>") && descriptor.equals("()V")) {
                state = 3;
                return; // 不 emit 构造函数
            }
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        };
    }


    @Override
    public Consumer<MethodVisitor> visitTypeInsn(int opcode, String type) {
        return mv -> {
            if (opcode == Opcodes.NEW &&
                    type.equals("net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader")) {
                patching = true;
                state = 1;
            } else {
                mv.visitTypeInsn(opcode, type);
            }

        };
    }

    @Override
    public Consumer<MethodVisitor> visitInsn(int opcode) {
        return methodVisitor -> {
            if (patching && state == 1 && opcode == Opcodes.DUP) {
                state = 2;
                return;
            }
            methodVisitor.visitInsn(opcode);
        };
    }


    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (!(
                name.equals("locatorClassLoader") &&
                        owner.equals("net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer") &&
                        descriptor.equals("Lnet/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader;") &&
                        state == 3
        )) {
            return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor); // ✅ 这是对的

        }
//

        return mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC,
                    "cc/zyycc/common/bridge/BridgeHolder",
                    "getSpiLoader",
                    "()Lcc/zyycc/common/bridge/ForgeSpiBridge;",
                    false);

            mv.visitMethodInsn(INVOKEVIRTUAL,
                    "cc/zyycc/common/bridge/ForgeSpiBridge",
                    "getClassLoader",
                    "()Ljava/lang/ClassLoader;",
                    false);

            mv.visitFieldInsn(PUTFIELD,
                    "net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer",
                    "locatorClassLoader",
                    "Ljava/lang/ClassLoader;");

//            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn("name:" + name + "owner:" + owner + " descriptor:" + descriptor + " line:" + line);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            line++;
            patching = false;
            state = 0;

////
////            // 加载 this
////            mv.visitVarInsn(Opcodes.ALOAD, 0);
////            // 调用 Hook.saveDiscoverer(Object)
////            mv.visitMethodInsn(
////                    Opcodes.INVOKESTATIC,
////                    "cc/zyycc/forge/debug/AgentHook",
////                    "saveDiscoverer",
////                    "(Ljava/lang/Object;)V",
////                    false
////            );
//
//        };
        };
    }
}
