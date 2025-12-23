package cc.zyycc.agent.transformer;

import cc.zyycc.agent.ClasspathAgent;
import cc.zyycc.agent.transformer.scan.ReflectionPool;
import cc.zyycc.bridge.BridgeManager;
import org.objectweb.asm.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.CodeSource;
import java.security.ProtectionDomain;


import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


public class TestTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!className.equals("com/comphenix/protocol/injector/netty/ChannelInjector")) {
            return null;
        }


        try {
            // ClasspathAgent.dump(className, classfileBuffer);

            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS) {
                @Override
                protected String getCommonSuperClass(String t1, String t2) {
                    return "java/lang/Object"; // 防止 ASM 去加载外部类
                }
            };


            ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                 String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (mv == null) return null;
                    if (name.equals("inject")) {
                        return new MethodVisitor(Opcodes.ASM9, mv) {
                            @Override
                            public void visitCode() {
                                super.visitCode();

                                // ALOAD 0 (this)
                                mv.visitVarInsn(Opcodes.ALOAD, 0);

                                // 取字段 this.networkManager （字段描述符用 Object，最通用）
                                mv.visitFieldInsn(
                                        Opcodes.GETFIELD,
                                        "com/comphenix/protocol/injector/netty/ChannelInjector",
                                        "networkManager",
                                        "Ljava/lang/Object;"
                                );

                                // 调用 agent.Guard.isLoginPhase(Object)Z
                                mv.visitMethodInsn(
                                        Opcodes.INVOKESTATIC,
                                        "cc/zyycc/bk/asm/util/Guard",
                                        "isLoginPhase",
                                        "(Ljava/lang/Object;)Z",
                                        false
                                );

                                Label cont = new Label();
                                mv.visitJumpInsn(Opcodes.IFEQ, cont);

                                // return false;
                                mv.visitInsn(Opcodes.ICONST_0);
                                mv.visitInsn(Opcodes.IRETURN);

                                mv.visitLabel(cont);
                            }


                        };
                    }
                    return mv;
                }

            };


            cr.accept(cv, 0);
            byte[] byteArray = cw.toByteArray();
          //  ClasspathAgent.dump(className, byteArray);
            return byteArray;
        } catch (
                Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private static String extractPluginName(ProtectionDomain pd) {
        try {
            CodeSource src = pd.getCodeSource();
            if (src == null || src.getLocation() == null) {
                return "unknown";
            }
            String path = src.getLocation().getPath();

            String fileName = new File(path).getName();
            // 去掉版本后缀
            int dashIndex = fileName.indexOf('-');
            if (dashIndex != -1) {
                fileName = fileName.substring(0, dashIndex);
            }
            return fileName.replace(".jar", "");
        } catch (Throwable e) {
            return "unknown";
        }
    }


}




