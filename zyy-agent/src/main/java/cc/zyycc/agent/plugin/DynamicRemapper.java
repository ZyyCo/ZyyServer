package cc.zyycc.agent.plugin;


import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicRemapper extends ClassRemapper {


    public DynamicRemapper(ClassVisitor cv, String currentClassName, PluginRemapper remapper) {
        super(Opcodes.ASM9, cv, remapper);
        this.className = currentClassName;

    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {

        if (superName != null) {
            superName = remapper.mapType(superName);
        }
        // remap 接口
        String[] mappedInterfaces = interfaces != null
                ? remapper.mapTypes(interfaces)
                : null;

        // remap 签名
        String mappedSignature = signature != null
                ? remapper.mapSignature(signature, false)
                : null;

        super.visit(
                version,
                access,
                name,
                mappedSignature,
                superName,
                mappedInterfaces
        );


//        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    protected MethodVisitor createMethodRemapper(final MethodVisitor methodVisitor) {
        return new PluginMethodRemapper(api, methodVisitor, remapper);
    }


//    //内部类
//    @Override
//    public void visitInnerClass(String name, String outerName, String innerName, int access) {
//        name = remapper.map(name);
//        outerName = remapper.map(outerName);
//        checkLeak("visitInnerClass name:", name);
//        checkLeak("visitInnerClass outerName:", outerName);
//        super.visitInnerClass(
//                name,
//                outerName,
//                innerName, access);
//    }
//
//    //验证阶段 (Verifier)
//    @Override
//    public FieldVisitor visitField(int access, String name, String desc,
//                                   String signature, Object value) {
//
//        desc = remapper.mapDesc(desc);
//        signature = remapper.map(signature);
//        checkLeak("visitField desc:", desc);
//        checkLeak("visitField signature:", signature);
//
//        if (value instanceof String) {
//            String str = (String) value;
//            checkLeak("visitField value:", str);
//        }
//        return super.visitField(access, name, desc,
//                signature == null ? null : signature, value);
//    }
//
//    @Override
//    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
//        checkLeak("visitRecordComponent name:", name);
//        checkLeak("visitRecordComponent desc:", descriptor);
//        checkLeak("visitRecordComponent signature:", signature);
//        return super.visitRecordComponent(name, descriptor, signature);
//    }
//
//
//    @Override
//    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
//        checkLeak("visitTypeAnnotation descriptor:", descriptor);
//        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
//    }
//
//    @Override
//    public void visitAttribute(Attribute attr) {
//        if ("Signature".equals(attr.type)) {
//            System.out.println("啊Signature");
//        }
//        super.visitAttribute(attr);
//    }
//
//    //注解
//    @Override
//    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//        desc = remapper.mapDesc(desc);
//        checkLeak("visitAnnotation desc:", desc);
//        AnnotationVisitor av = super.visitAnnotation(desc, visible);
//        return new AnnotationVisitor(Opcodes.ASM9, av) {
//            @Override
//            public void visit(String name, Object value) {
//                if (value instanceof Type) {
//                    String mapped = remapper.mapDesc(((Type) value).getDescriptor());
//                    value = Type.getType(mapped);
//                    checkLeak("visitAnnotation inner Type:", mapped);
//                } else if (value instanceof String && ((String) value).contains("net/minecraft/server/v1_16_R3")) {
//                    checkLeak("visitAnnotation inner String:", (String) value);
//                }
//                super.visit(name, value);
//            }
//        };
//    }
//
//
//    //外部类引用
//    @Override
//    public void visitOuterClass(String owner, String name, String desc) {
//        owner = remapper.map(owner);
//        desc = remapper.mapMethodDesc(desc);
//        checkLeak("visitAnnotation name:", name);
//        checkLeak("visitAnnotation owner:", owner);
//        checkLeak("visitAnnotation desc:", desc);
//        super.visitOuterClass(owner, name, desc);
//    }
//
//
//
//    @Override
//    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
//
//        desc = remapper.mapMethodDesc(desc);
//        signature = remapper.mapDesc(signature);
//        checkLeak("visitMethod desc:", desc);
//        checkLeak("visitMethod methodName:", methodName);
//        checkLeak("visitMethod signature:", signature);
//        if (exceptions != null) {
//            for (String exception : exceptions) {
//                checkLeak("exception:", exception);
//            }
//        }
//
//        MethodVisitor mv = super.visitMethod(access, methodName,
//                desc,
//                signature == null ? null : signature,
//                remapArray(exceptions));
//
//        return new MethodVisitor(Opcodes.ASM9, mv) {
//            @Override
//            public void visitTypeInsn(int opcode, String type) {
//                String map = remapper.map(type);
//                checkLeak("visitMethod visitTypeInsn type:", map);
//                super.visitTypeInsn(opcode, map);
//            }
//
//
//            @Override
//            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//                //字段调用
////                if(owner.startsWith("net/minecraft/") && !owner.contains("v1_16_R3")){
////                    System.out.println("owner: " + owner);
////                }
//
//                name = remapper.mapFiled(owner, name);
//                owner = remapper.map(owner);
//                desc = remapper.mapParams(desc);
//
////                System.out.println("visitFieldInsn" + owner + "/" + name + " " + desc);
//                checkLeak("visitMethod visitFieldInsn desc:", desc);
//                checkLeak("visitMethod visitFieldInsn name:", name);
//                checkLeak("visitMethod visitFieldInsn owner:", owner);
//                super.visitFieldInsn(opcode, owner, name, desc);
//            }
//
//            @Override
//            public void visitMethodInsn(int opcode, String owner, String methodName,
//                                        String descriptor, boolean isInterface) {
////                if(owner.equals("org/bukkit/GameMode") && methodName.equals("values")){
////                    System.out.println("GameMode/ordinal");
////                }
//                if (owner.startsWith("net/minecraft/server/v1_")) {
//                    MethodMappingEntry resultEntry = remapper.mapMethod(owner, methodName, descriptor);
//                    if (resultEntry != null) {
//                        owner = resultEntry.getClassName();
//                        methodName = resultEntry.getMethodName();
//                        descriptor = resultEntry.getMethodDesc();
//                    }
//                } else if (descriptor.contains("Lnet/minecraft/server/v1_")) {
//                    descriptor = remapper.mapMethodDesc(descriptor);
//                }
//
////                System.out.println(owner + "/" + methodName + " " + descriptor);
//                checkLeak("visitMethod visitMethodInsn desc:", descriptor);
//                checkLeak("visitMethod visitMethodInsn methodName:", methodName);
//                checkLeak("visitMethod visitMethodInsn Owner:", owner);
//
//                super.visitMethodInsn(opcode,
//                        owner,
//                        methodName,
//                        descriptor,
//                        isInterface);
//
//
//            }
//
//            @Override
//            public void visitLdcInsn(Object cst) {
//                if (cst instanceof String) {
//                    String str = (String) cst;
//                    checkLeak("visitMethod visitLdcInsn cst:", str);
//                    if (str.contains("net/minecraft/server/v1_16_R3/")) {
//                        cst = str.replace("net/minecraft/server/v1_16_R3/", "net/minecraft/");
//                    }
//                } else if (cst instanceof Type) {
//                    Type type = (Type) cst;
//                    String descriptor = type.getDescriptor();
//                    // 替换类路径
//                    checkLeak("visitMethod visitLdcInsn descriptor:", descriptor);
//                    if (descriptor.contains("net/minecraft/server/v1_16_R3")) {
//                        cst = Type.getType(descriptor.replace("net/minecraft/server/v1_16_R3/", "net/minecraft/"));
//                    }
//                }
//                super.visitLdcInsn(cst);
//            }
//
//            @Override
//            public void visitMultiANewArrayInsn(String desc, int dims) {
//                String s = remapper.mapDesc(desc);
//                checkLeak("visitMethod visitMultiANewArrayInsn desc:", s);
//                super.visitMultiANewArrayInsn(s, dims);
//            }
//
//
//            @Override
//            public void visitLocalVariable(String name, String desc, String signature,
//                                           Label start, Label end, int index) {
//                desc = remapper.mapDesc(desc);
//                signature = remapper.mapDesc(signature);
//                checkLeak("visitMethod visitLocalVariable desc:", desc);
//                checkLeak("visitMethod visitLocalVariable signature:", signature);
//                super.visitLocalVariable(name,
//                        remapper.mapDesc(desc),
//                        signature,
//                        start, end, index);
//            }
//
//            @Override
//            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    /// /                System.out.println("visitTryCatchBlock" + type);
//                type = remapper.map(type);
//                checkLeak("visitTryCatchBlock type:", type);
//                super.visitTryCatchBlock(start, end, handler, type);
//            }
//
//            @Override
//            public AnnotationVisitor visitAnnotation(String descriptor, final boolean visible) {
//                descriptor = remapper.mapDesc(descriptor);
//                checkLeak("visitAnnotation type:", descriptor);
//                return super.visitAnnotation(descriptor, visible);
//            }
//
//            @Override
//            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
//                descriptor = remapper.mapDesc(descriptor);
//                checkLeak("visitTypeAnnotation:", descriptor);
//                return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
//            }
//
//            @Override
//            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
//                checkLeak("visitParameterAnnotation:", descriptor);
//                return super.visitParameterAnnotation(parameter, descriptor, visible);
//            }
//
//            public void visitAttribute(Attribute attr) {
//                if ("Signature".equals(attr.type)) {
//                    System.out.println("啊Signature");
//                }
//                super.visitAttribute(attr);
//            }
//
//
//            @Override
//            public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
//                checkLeak("visitInvokeDynamicInsn:", descriptor);
//                checkLeak("bootstrapMethodHandle:", bootstrapMethodHandle.getDesc());
//                checkLeak("bootstrapMethodHandle:", bootstrapMethodHandle.getOwner());
//                checkLeak("bootstrapMethodHandle:", bootstrapMethodHandle.getName());
//                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
//            }
//
//        };
//
//
//    }
//
//    @Override
//    public void visitNestHost(String nestHost) {
//        checkLeak("visitNestHost:", nestHost);
//        super.visitNestHost(remapper.map(nestHost));
//    }
//
//
//    @Override
//    public void visitNestMember(String nestMember) {
//        checkLeak("visitNestMember:", nestMember);
//        super.visitNestMember(remapper.map(nestMember));
//    }
//
//    @Override
//    public ModuleVisitor visitModule(String name, int access, String version) {
//        checkLeak("visitModule:", name);
//        checkLeak("visitModule:", version);
//        return super.visitModule(name, access, version);
//    }
//
//    @Override
//    public void visitPermittedSubclass(String permittedSubclass) {
//        checkLeak("visitRecordComponent permittedSubclass:", permittedSubclass);
//        super.visitPermittedSubclass(permittedSubclass);
//    }
//
//
//    private String[] remapArray(String[] arr) {
//        if (arr == null) return null;
//        String[] newArr = new String[arr.length];
//        for (int i = 0; i < arr.length; i++) {
//            checkLeak("修改前remapArray", newArr[i]);
//            newArr[i] = remapper.map(arr[i]);
//            checkLeak("remapArray", newArr[i]);
//        }
//        return newArr;
//    }
    private void checkLeak(String context, String s) {
        if (s != null && (s.contains("net/minecraft/server/v1_"))) {
            System.out.println("[RemapLeak@" + context + "] " + s);
        }
    }


}
