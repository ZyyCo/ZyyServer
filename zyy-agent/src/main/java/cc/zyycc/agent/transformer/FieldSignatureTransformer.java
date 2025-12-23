package cc.zyycc.agent.transformer;

import cc.zyycc.agent.transformer.scan.ReflectionPool;
import cc.zyycc.bridge.BridgeManager;
import org.objectweb.asm.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static cc.zyycc.agent.transformer.TransformerProvider.loaderToPlugin;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


public class FieldSignatureTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!"org.bukkit.plugin.java.PluginClassLoader".equals(loader.getClass().getName())) {
            return null;
        }
        if (!ReflectionPool.containsReflection(classfileBuffer)) {
            return null;
        }

        String pluginName = loaderToPlugin.get(loader);
        if (pluginName == null) {
            pluginName = extractPluginName(protectionDomain);
            loaderToPlugin.put(loader, pluginName);
            BridgeManager.LOADER_REGISTRY.put(pluginName, loader);
        }
        try {
            // ClasspathAgent.dump(className, classfileBuffer);

            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(COMPUTE_MAXS) {
                @Override
                protected String getCommonSuperClass(String t1, String t2) {
                    return "java/lang/Object"; // 防止 ASM 去加载外部类
                }
            };

            String finalPluginName = pluginName;
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor,
                                                 String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (mv == null) return null;

                    return new MethodVisitor(Opcodes.ASM9, mv) {



                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
                            if (opcode == Opcodes.INVOKEVIRTUAL
                                    && name.equals("loadClass")
                                    && owner.equals("java/lang/ClassLoader")
                                    && desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        "cc/zyycc/common/bridge/SafeClassForNameBridge",
                                        "loadClass",
                                        "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;", false);
                                return;
                            }
                            if (opcode == Opcodes.INVOKESTATIC
                                    && "java/lang/Class".equals(owner)
                                    && "forName".equals(name)) {

                                if (desc.equals("(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;")) {
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                            "cc/zyycc/common/bridge/SafeClassForNameBridge",
                                            "forName",
                                            desc,
                                            false);
                                    return;
                                }
                                // 一参数版本：forName(String)
                                else if (!desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
                                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                                    return;
                                }
                                // 插入新方法调用
                                super.visitLdcInsn(finalPluginName);
                                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        "cc/zyycc/common/bridge/SafeClassForNameBridge",
                                        "forName",
                                        "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;",
                                        false);
                                return;
                            }

                            //getMethod
                            if (opcode == Opcodes.INVOKEVIRTUAL
                                    && (name.equals("getMethod") || name.equals("getDeclaredMethod"))
                                    && owner.equals("java/lang/Class")) {

                                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        "cc/zyycc/common/bridge/SafeMethodBridge",
                                        "getMethod", // getMethod 或 getDeclaredMethod
                                        "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                                        false);
                            } else if (opcode == Opcodes.INVOKEVIRTUAL
                                    && (name.equals("getField") || name.equals("getDeclaredField"))
                                    && desc.equals("(Ljava/lang/String;)Ljava/lang/reflect/Field;")
                                    && owner.equals("java/lang/Class")) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        "cc/zyycc/common/bridge/SafeFiledBridge",
                                        name, // getField 或 getDeclaredField
                                        "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;",
                                        false);
                            } else {
                                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                            }
                        }


//                        @Override
//                        public void visitMaxs(int maxStack, int maxLocals) {
//                            System.out.println(className);
//                            super.visitMaxs(maxStack, maxLocals + localVarIndex);
//                        }
                    };
                }

            };


            cr.accept(cv, 0);
            return cw.toByteArray();
        } catch (Throwable t) {
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




