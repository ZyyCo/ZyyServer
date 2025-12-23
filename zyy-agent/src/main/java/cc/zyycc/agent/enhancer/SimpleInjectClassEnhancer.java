package cc.zyycc.agent.enhancer;


import cc.zyycc.agent.inject.InjectVisitMethod;
import cc.zyycc.agent.inject.method.InjectMethod;
import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SimpleInjectClassEnhancer extends BaseEnhancer<InjectVisitMethod> {

    protected final Map<String, String> allInjectVisitMethods = new ConcurrentHashMap<>();
    protected final Map<TargetMethod, List<InjectVisitMethod>> injectMethodMap = new ConcurrentHashMap<>();
    boolean needFrame = false;

    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String className, ClassLoader classLoader) {
        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                for (Map.Entry<TargetMethod, List<InjectVisitMethod>> target : injectMethodMap.entrySet()) {
                    target.getValue().forEach(injectMethod -> injectMethod.scanField(name, descriptor));
                }

                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                for (Map.Entry<TargetMethod, List<InjectVisitMethod>> target : injectMethodMap.entrySet()) {
                    TargetMethod targetMethod = target.getKey();
                    List<InjectVisitMethod> injectMethods = target.getValue();
                    if (targetMethod.isTargetMethod(name, desc)) {
                        for (InjectMethod injectMethod : target.getValue()) {
                            injectMethod.setTargetMethodDesc(targetMethod.getDesc());
                        }

                        return new InjectMethodVisitor(mv, className, access, desc, allInjectVisitMethods, injectMethods);
                    }

                }
                return mv;
            }


        };
    }

    @Override
    public boolean enhancer() {
        return false;
    }

    @Override
    public boolean needFrame() {
        return needFrame;
    }

    @Override
    public void addOrMerge(TargetMethod targetMethod, InjectVisitMethod injectVisitMethod) {
        List<InjectVisitMethod> injectMethods = injectMethodMap.get(targetMethod);
        if (injectMethods == null) {
            injectMethods = new ArrayList<>();
        }
        injectMethods.add(injectVisitMethod);
        injectMethodMap.put(targetMethod, injectMethods);
        if (injectVisitMethod.needFrame()) {
            needFrame = true;
        }
    }


}