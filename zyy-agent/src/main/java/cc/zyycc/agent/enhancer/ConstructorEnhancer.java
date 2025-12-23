package cc.zyycc.agent.inject.method;

import cc.zyycc.agent.enhancer.InjectVisitMethod;
import cc.zyycc.agent.enhancer.TargetMethod;
import cc.zyycc.agent.inject.IInjectVisitMethod;
import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConstructorEnhancer extends SimpleInjectClassEnhancer {
    private final String constructorDescriptor;
    private final InjectMethod constructorInject;


    public ConstructorEnhancer(String constructorDescriptor, InjectMethod constructorInject) {
        super(null);
        this.constructorDescriptor = constructorDescriptor;
        this.constructorInject = constructorInject;
    }


    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String className, ClassLoader classLoader) {

        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                constructorInject.scanField(name, descriptor);
                if (allInjectVisitMethods != null) {
                    for (InjectVisitMethod injectVisitMethod : allInjectVisitMethods) {
                        injectVisitMethod.scanField(name, descriptor);
                    }
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                Map<TargetMethod, List<IInjectVisitMethod>> targetMethod = transformerProvider.getTargetMethod();
                for (Map.Entry<TargetMethod, List<IInjectVisitMethod>> entry : targetMethod.entrySet()) {
                    TargetMethod method = entry.getKey();
                    if (method.isTargetMethod(name, desc)) {
                        List<IInjectVisitMethod> injectList = entry.getValue();
                        List<InjectVisitMethod> injectVisitMethods = new ArrayList<>();
                        for (IInjectVisitMethod iInjectVisitMethod : injectList) {
                            if (injectList instanceof InjectVisitMethod) {
                                injectVisitMethods.add((InjectVisitMethod) iInjectVisitMethod);
                            }
                            return new InjectMethodVisitor(mv, injectVisitMethods);
                        }

                    }
                }
                return mv;
            }


            @Override
            public void visitEnd() {

                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDescriptor,
                        null, null);
                mv.visitCode();
                // 调用 super()
                mv.visitVarInsn(Opcodes.ALOAD, 0);  // 将 this 压入栈
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

                insertBefore.injectCode(mv);
                constructorInject.injectCode(mv, className);

                //return
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(constructorInject.maxStack(), 1);
                mv.visitEnd();
                insertBefore.injectCodeAfter();
                super.visitEnd();
            }
        };

    }

    @Override
    public boolean enhancer() {
        return true;
    }


}

