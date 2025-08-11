package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.DeMethod;
import org.objectweb.asm.*;


public class AsmEnhancer {
    public static byte[] enhance(byte[] bytes, DeMethod deMethod) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);


        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {
//            @Override
//            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
//                if (name.equals("locatorClassLoader") && descriptor.equals("Lnet/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader;")) {
//                    System.out.println("[Patch] 修改 locatorClassLoader 字段类型为 ClassLoader");
//                    return super.visitField(access, name, "Ljava/lang/ClassLoader;", signature, value);
//                }
//                return super.visitField(access, name, descriptor, signature, value);
//            }
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                boolean isDescriptorMatched = true;
                if (deMethod.getDescriptor() != null) {
                    isDescriptorMatched = descriptor.equals(deMethod.getDescriptor());
                }


                if (deMethod.getTargetMethodName().equals(name) && isDescriptorMatched) {
                    return new MyMethodVisitor(Opcodes.ASM5,
                            super.visitMethod(access, name, descriptor, signature, exceptions),
                            deMethod.getiVisitCode());
                }

                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }





            @Override
            public void visitEnd() {
//                super.visitField(ACC_PRIVATE | Opcodes.ACC_FINAL,
//                        "locatorClassLoader",
//                        "Ljava/lang/ClassLoader;",
//                        null,
//                        null
//                ).visitEnd();
                super.visitEnd();
            }
        };

        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }

}
