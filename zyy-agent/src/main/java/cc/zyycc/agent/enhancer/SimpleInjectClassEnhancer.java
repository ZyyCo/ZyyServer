package cc.zyycc.agent.inject.method;


import cc.zyycc.agent.enhancer.*;
import cc.zyycc.agent.inject.IInjectVisitMethod;
import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import cc.zyycc.agent.transformer.ClassEnhancer;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;

import java.util.*;


public abstract class SimpleInjectClassEnhancer implements ClassEnhancer {
    protected InsertBefore insertBefore;

    protected List<InjectVisitMethod> allInjectVisitMethods = new ArrayList<>();


    public SimpleInjectClassEnhancer(InsertBefore insertBefore) {
        if (insertBefore == null) {
            this.insertBefore = defaultInsertBefore();
        } else {
            this.insertBefore = insertBefore;
        }

//        for (TargetMethod targetMethod : targetMethods) {
//            allInjectVisitMethods.addAll(Arrays.asList(targetMethod.injectMethodEnhancer));
//        }
    }


    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String className, ClassLoader classLoader) {
        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                for (InjectVisitMethod injectVisitMethod : allInjectVisitMethods) {
                    injectVisitMethod.scanField(name, descriptor);
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
                for (InjectVisitMethod injectVisitMethod : allInjectVisitMethods) {
                    injectVisitMethod.injectCode(className);
                }

                super.visitEnd();
            }
        };
    }


    public InsertBefore defaultInsertBefore() {
        return new InsertBefore() {
            @Override
            public void injectCode(MethodVisitor mv) {
            }


            @Override
            public void injectCodeAfter() {
            }
        };
    }
}