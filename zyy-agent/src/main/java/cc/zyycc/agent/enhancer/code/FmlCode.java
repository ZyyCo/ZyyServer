package cc.zyycc.agent.enhancer.code;

import cc.zyycc.agent.enhancer.MyMethodVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class FmlCode implements IVisitCode {
    @Override
    public Consumer<MethodVisitor> code() {
        return null;
    }

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        return mv -> {
            if (!(opcode == Opcodes.INVOKEINTERFACE &&
                    owner.equals("java/util/stream/Stream") &&
                    name.equals("forEach") &&
                    descriptor.equals("(Ljava/util/function/Consumer;)V"))) {
                return;
            }
            System.out.println("名字" + name + "   owner" + owner);
// System.err.println("📦 handler = " + this.launchHandlerLookup.get(launchTarget));
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("📦 handler = ");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitVarInsn(ALOAD, 0); // this
            mv.visitFieldInsn(GETFIELD, "cpw/mods/modlauncher/LaunchServiceHandler", "launchHandlerLookup", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 2); // launchTarget 是第二个参数（slot 2）
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


//             注入在 forEach(builder::addTransformationPath) 后面
//            if (opcode == Opcodes.INVOKEINTERFACE &&
//                    owner.equals("java/util/stream/Stream") &&
//                    name.equals("forEach") &&
//                    descriptor.equals("(Ljava/util/function/Consumer;)V")) {
//
//                System.out.println("⚡ 检测到 forEach 调用，插入打印代码...");
//
//                // System.out.println("== 我插进来了，builder 是：" + builder);
//                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                mv.visitVarInsn(Opcodes.ALOAD, 3); // builder 一般是局部变量 index = 1，必要时换成 2/3 试试
//
//
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
//                        "java/lang/String",
//                        "valueOf",
//                        "(Ljava/lang/Object;)Ljava/lang/String;",
//                        false);
//                mv.visitLdcInsn("== 我插进来了，builder 是：");
//                mv.visitInsn(Opcodes.SWAP);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "java/lang/String",
//                        "concat",
//                        "(Ljava/lang/String;)Ljava/lang/String;",
//                        false);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "java/io/PrintStream",
//                        "println",
//                        "(Ljava/lang/String;)V",
//                        false);
//            }
        };
    }

    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        return null;
    }


    private static void printlnMap(MyMethodVisitor mv) {
        // System.err.println("🌟 打印 launchHandlerLookup keys:");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("🌟 打印 launchHandlerLookup keys:");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

// Map<String, LaunchServiceHandlerDecorator> map = this.launchHandlerLookup;
        mv.visitVarInsn(ALOAD, 0); // this
        mv.visitFieldInsn(GETFIELD, "cpw/mods/modlauncher/LaunchServiceHandler", "launchHandlerLookup", "Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;", true);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitVarInsn(ASTORE, 4); // Iterator<String> it

        Label loopStart = new Label();
        Label loopEnd = new Label();

        mv.visitLabel(loopStart);
// if (!it.hasNext()) goto loopEnd;
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitJumpInsn(IFEQ, loopEnd);

// String key = (String) it.next();
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitVarInsn(ASTORE, 5);

// System.err.println("🔑 found launchTarget: " + key);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("🔑 found launchTarget: ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

// goto loopStart;
        mv.visitJumpInsn(GOTO, loopStart);
        mv.visitLabel(loopEnd);

    }


}
