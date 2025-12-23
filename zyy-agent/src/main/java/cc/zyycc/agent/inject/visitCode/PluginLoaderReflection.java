package cc.zyycc.agent.inject.visitCode;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class JavaPluginLoaderReflection extends InjectVisitCode {


    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> {
            if (opcode == Opcodes.INVOKEVIRTUAL
                    && name.equals("loadClass")
                    && owner.equals("java/lang/ClassLoader")
                    && descriptor.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/SafeClassForNameBridge",
                        "loadClass",
                        "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;", false);
                return;
            }


            if (opcode == Opcodes.INVOKESTATIC
                    && "java/lang/Class".equals(owner)
                    && "forName".equals(name)
                    && descriptor.startsWith("(Ljava/lang/String;)")) {

                mv.visitLdcInsn(context.getPluginName());

                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/SafeClassForNameBridge",
                        "forName",
                        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;",
                        false);
                //getMethod

            } else if (opcode == Opcodes.INVOKEVIRTUAL
                    && (name.equals("getMethod") || name.equals("getDeclaredMethod"))
                    && owner.equals("java/lang/Class")) {

                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/SafeMethodBridge",
                        "getMethod", // getMethod 或 getDeclaredMethod
                        "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        false);
            } else if (opcode == Opcodes.INVOKEVIRTUAL
                    && (name.equals("getField") || name.equals("getDeclaredField"))
                    && descriptor.equals("(Ljava/lang/String;)Ljava/lang/reflect/Field;")
                    && owner.equals("java/lang/Class")) {


                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/common/bridge/SafeFiledBridge",
                        name, // getField 或 getDeclaredField
                        "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;",
                        false);

            } else {
                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        };
    }
}
