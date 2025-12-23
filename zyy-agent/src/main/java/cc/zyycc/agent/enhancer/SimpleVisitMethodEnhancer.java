package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import cc.zyycc.agent.inject.visitCode.MyMethodVisitor;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleVisitMethodEnhancer extends BaseEnhancer<InjectVisitCode> {


    private boolean enhancer;

    public SimpleVisitMethodEnhancer() {

    }

    public Map<TargetMethod, InjectVisitCode> targetMethods = new ConcurrentHashMap<>();
    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformer, String pluginName, String className, ClassLoader classLoader) {

        return new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
                for (Map.Entry<TargetMethod, InjectVisitCode> entry : targetMethods.entrySet()) {
                    TargetMethod targetMethod = entry.getKey();
                    if (targetMethod.isTargetMethod(methodName, descriptor)) {
                        MethodVisitor base = super.visitMethod(access, methodName, descriptor, signature, exceptions);
                        MyMethodVisitor wrapped = new MyMethodVisitor(Opcodes.ASM9, base, entry.getValue(), pluginName, classLoader, methodName);
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

    @Override
    public void addOrMerge(TargetMethod targetMethod, InjectVisitCode injectVisitCode) {
        targetMethods.put(targetMethod, injectVisitCode);
    }


}
