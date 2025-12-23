package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.inject.visitCode.MyMethodVisitor;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class ModDiscoverer$LocatorClassLoaderCode extends InjectVisitCode {

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> {

            if (opcode == Opcodes.INVOKESTATIC && name.equals("getSystemClassPathURLs")) {
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/net/URL");
            } else if (
                    opcode == Opcodes.INVOKESTATIC
                            && descriptor.equals("()Ljava/lang/ClassLoader;")
                            && name.equals("getSystemClassLoader")
            ) {

                mv.visitFieldInsn(GETSTATIC, "cc/zyycc/common/loader/MyLoader", "ZYY", "Lcc/zyycc/common/loader/MyLoader;");

                mv.visitMethodInsn(INVOKESTATIC, "cc/zyycc/common/loader/LoaderManager",
                        "getClassLoader",
                        "(Lcc/zyycc/common/loader/MyLoader;)Ljava/lang/ClassLoader;",
                        false);

            } else {
                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }


            //   patched = true;
        };
    }


    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor, MyMethodVisitor context) {
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

    @Override
    public boolean needFrame() {
        return false;
    }
}
