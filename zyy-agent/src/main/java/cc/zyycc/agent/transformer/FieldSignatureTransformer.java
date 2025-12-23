package cc.zyycc.agent.transformer;

import cc.zyycc.agent.ClasspathAgent;
import cc.zyycc.agent.plugin.FiledRemapper;

import cc.zyycc.agent.plugin.PluginRemapper;
import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;


import java.lang.instrument.ClassFileTransformer;

import java.security.ProtectionDomain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.objectweb.asm.Opcodes.ASM9;

public class AAATransformer implements ClassFileTransformer {

    public AAATransformer() {

    }


    // 你可记录允许的 ClassLoader 简名，或直接用 instanceof 判断


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!className.equals("org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftWorld")
        && !className.equals("org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftServer")) {
            return null;
        }

        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            // 写法1：不改栈图（更快，不动 frames）
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS) {
                @Override
                protected String getCommonSuperClass(String t1, String t2) {
                    return "java/lang/Object"; // 防止 ASM 去加载外部类
                }
            };


//            ClassVisitor cv = new ClassRemapper(cw, new FiledRemapper());

            ClassVisitor cv = new ClassVisitor(ASM9, cw) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                 String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (mv == null) return null;

                    return new MethodVisitor(ASM9, mv) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                            if (descriptor.equals("Lnet/minecraft/world/storage/ServerWorldInfo;")) {
                                descriptor = "Lnet/minecraft/world/storage/IServerWorldInfo;";
                            }
                            //字段里的字段
                            if(owner.equals("net/minecraft/world/storage/ServerWorldInfo")){
                                owner = "net/minecraft/world/storage/IServerWorldInfo";
                            }
                            super.visitFieldInsn(opcode, owner, name, descriptor);
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
                            if (owner.equals("net/minecraft/world/storage/ServerWorldInfo")
                                    && (name.startsWith("func") || name.startsWith("field"))
                                  ){
                                opcode = Opcodes.INVOKEINTERFACE; // 强制切换到接口调用
                                owner = "net/minecraft/world/storage/IServerWorldInfo";
                                isInterface = true;
                            }
                            super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                        }

//                        @Override
//                        public void visitTypeInsn(int opcode, String type) {
//                            if (type.equals("net/minecraft/world/storage/ServerWorldInfo")) {
//                                type = "net/minecraft/world/storage/IServerWorldInfo";
//                            }
//                            super.visitTypeInsn(opcode, type);
//                        }

                    };
                }
            };


            // 建议：SKIP_DEBUG 可以略过局部变量名/行号，加速；不建议 SKIP_FRAMES（除非确实踢 verifier）
            cr.accept(cv, 0);

            byte[] bytes = cw.toByteArray();

            ClasspathAgent.dump(className, bytes);


            return bytes;
        } catch (Throwable t) {
            t.printStackTrace();
            // 返回 null 让 JVM 用原字节码（保守）
            return null;
        }
    }


}




