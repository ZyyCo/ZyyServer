package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.enhancer.TargetMethod;
import cc.zyycc.agent.inject.IInjectVisitMethod;
import cc.zyycc.agent.enhancer.ClassEnhancer;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;

import java.util.List;
import java.util.Map;

public class SimpleVisitMethodEnhancer implements ClassEnhancer {


    private boolean enhancer;

    public SimpleVisitMethodEnhancer() {

    }


    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformer, String pluginName, String className, ClassLoader classLoader) {

        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
                Map<TargetMethod, List<IInjectVisitMethod>> targetMethod = transformer.getTargetMethod();
                for (TargetMethod method : targetMethod.keySet()) {
                    if (method.isTargetMethod(methodName, descriptor)) {
                        MethodVisitor base = super.visitMethod(access, methodName, descriptor, signature, exceptions);
                        IInjectVisitMethod iInjectVisitMethod = targetMethod.get(method).get(0);
                        MyMethodVisitor wrapped = new MyMethodVisitor(Opcodes.ASM9, base, (InjectVisitCode) iInjectVisitMethod, pluginName, classLoader, methodName);
                        enhancer = wrapped.isEnhancer();
                        return wrapped;
                    }
                }


                return super.visitMethod(access, methodName, descriptor, signature, exceptions);
            }
        };
    }

    @Override
    public boolean enhancer() {
        return enhancer;
    }


}
