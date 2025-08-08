package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class ModDiscoverer$LocatorClassLoaderCode implements IVisitCode {
    public static int line = 0;
    int state = 0;
    boolean patched = false;

    @Override
    public Consumer<MethodVisitor> code() {
        return null;
    }


    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        return mv -> {

            if(opcode == Opcodes.INVOKESTATIC  && name.equals("getSystemClassPathURLs")){
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/net/URL");
            }else
                if (
                    opcode == Opcodes.INVOKESTATIC
                            && descriptor.equals("()Ljava/lang/ClassLoader;")
                            && name.equals("getSystemClassLoader")
            ) {

                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/BridgeHolder",
                        "getInstance",
                        "()Lcc/zyycc/common/bridge/PluginLoaderBridge;",
                        false);

                mv.visitFieldInsn(Opcodes.GETFIELD,
                        "cc/zyycc/common/bridge/PluginLoaderBridge",
                        "classLoader",
                        "Ljava/lang/ClassLoader;");

            }else {
                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }


            //   patched = true;
        };
    }


    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor);
//        if (!(
//                name.equals("locatorClassLoader") &&
//                        owner.equals("net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer") &&
//                        descriptor.equals("Lnet/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader;") &&
//                        state == 3
//        )) {
//            return mv -> mv.visitFieldInsn(opcode, owner, name, descriptor); // ✅ 这是对的
//
//        }
//
//
//        return mv -> {
//            mv.visitVarInsn(Opcodes.ALOAD, 0);
//            mv.visitMethodInsn(INVOKESTATIC,
//                    "cc/zyycc/common/bridge/BridgeHolder",
//                    "getSpiLoader",
//                    "()Lcc/zyycc/common/bridge/ForgeSpiBridge;",
//                    false);
//
//            mv.visitMethodInsn(INVOKEVIRTUAL,
//                    "cc/zyycc/common/bridge/ForgeSpiBridge",
//                    "getClassLoader",
//                    "()Ljava/lang/ClassLoader;",
//                    false);
//
//            mv.visitFieldInsn(PUTFIELD,
//                    "net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer",
//                    "locatorClassLoader",
//                    "Ljava/lang/ClassLoader;");
//
////            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
////            mv.visitLdcInsn("name:" + name + "owner:" + owner + " descriptor:" + descriptor + " line:" + line);
////            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
////            line++;
//            patching = false;
//            state = 0;
//
//////


        //};
    }
}
