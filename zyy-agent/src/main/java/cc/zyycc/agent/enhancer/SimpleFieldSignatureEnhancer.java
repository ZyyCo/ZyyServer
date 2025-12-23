package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.transformer.TransformerProvider;
import cc.zyycc.agent.util.FieldDescHandle;
import cc.zyycc.agent.util.FieldHandle;
import org.objectweb.asm.*;

public class SimpleFieldSignatureEnhancer implements ClassEnhancer<IInjectMode> {


    private final FieldHandle[] fieldSignatures;

    public SimpleFieldSignatureEnhancer(FieldHandle... fieldSignatures) {
        this.fieldSignatures = fieldSignatures;
    }

    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String targetClassName, ClassLoader classLoader) {
        return new ClassVisitor(Opcodes.ASM9, cw) {

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                for (FieldHandle fieldSignature : fieldSignatures) {
                    if (fieldSignature.match(name, descriptor)) {
                        descriptor = fieldSignature.modifyDesc();
                    }
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {


                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, mv) {

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        for (FieldHandle handle : fieldSignatures) {
                            boolean match = handle.match(owner, name, descriptor);
                            if (match) {
                                if (handle.getDesc() != null) {
                                    descriptor = handle.modifyDesc();
                                }
                                if (handle.getOriginalName() != null) {
                                    name = handle.modifyName();
                                }
                                if (handle.getOwner() != null) {
                                    owner = handle.modifyOwner();
                                }
                            } else
                                //第二层字段field_237342_b_
                                if (name.startsWith("field") && handle.matchOwner(descriptor)) {
                                    if (handle.getDesc() != null) {
                                        descriptor = handle.modifyDesc();
                                    }
                                }
                        }
                        super.visitFieldInsn(opcode, owner, name, descriptor);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
                        for (FieldHandle handle : fieldSignatures) {
                            if (handle instanceof FieldDescHandle) {
                                if (((FieldDescHandle) handle).twoStageMatch(owner)) {
                                    owner = ((FieldDescHandle) handle).getNewOwner();
                                    if (((FieldDescHandle) handle).isReverseInterface()) {
                                        isInterface = !isInterface;
                                        opcode = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
                                    }
                                }
                            }

                        }
                        super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                    }
                };
            }


        };
    }

    @Override
    public boolean enhancer() {
        return true;
    }

    @Override
    public void addOrMerge(TargetMethod targetMethod, IInjectMode injectMode) {

    }

    @Override
    public Object identityKey() {
        return getClass();
    }


}
