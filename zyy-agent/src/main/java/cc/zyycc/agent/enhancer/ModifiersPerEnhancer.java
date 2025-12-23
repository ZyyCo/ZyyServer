package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;


public class ModifiersPerEnhancer extends FieldEnhancer<InjectVisitCode> {


    private final int modifier;
    private final String[] modifierNames;
    private final int type;

    public ModifiersPerEnhancer(int modifier, int type, String... modifierNames) {
        this.modifierNames = modifierNames;
        this.modifier = modifier;
        this.type = type;
    }

    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider provider, String pluginName, String className, ClassLoader classLoader) {
        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if (type == 0) {
                    if (checkName(name, access)) {
                        access = (access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) | modifier;
                    }
                }

                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (type == 1) {
                    if (checkName(name, access)) {
                        access = (access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) | modifier;
                    }
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };

    }

    @Override
    public void addOrMerge(TargetMethod targetMethod, InjectVisitCode injectVisitCode) {

    }


    public boolean checkName(String name, int access) {
        if (modifierNames == null || modifierNames.length == 0) return false;
        if (access != modifier) {
            for (String modifierName : modifierNames) {
                if (modifierName.equals(name)) {
                    enhancer = true;
                    return true;
                }
            }
        }
        return false;
    }
}
