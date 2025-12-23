package cc.zyycc.agent.transformer;

import cc.zyycc.agent.ClasspathAgent;
import cc.zyycc.agent.enhancer.*;
import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.inject.InjectVisitMethod;
import cc.zyycc.agent.inject.Injects;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunction;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunctionBase;
import cc.zyycc.agent.inject.method.InjectPoint;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import cc.zyycc.agent.transformer.scan.ConstantPool;
import cc.zyycc.agent.transformer.scan.SimpleScan;
import cc.zyycc.agent.transformer.scan.ScanStrategy;
import cc.zyycc.agent.util.FieldHandle;
import cc.zyycc.bridge.BridgeManager;
import cc.zyycc.common.VersionInfo;
import cc.zyycc.util.StrClassName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class TransformerProvider implements ClassFileTransformer {

    public static final Map<ClassLoader, String> loaderToPlugin = new ConcurrentHashMap<>();

    private final String superClass;
    private final ScanStrategy scan;
    private boolean already;
    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    private final Set<ClassEnhancer<?>> enhancers;

    public TransformerProvider(ScanStrategy scan, Set<ClassEnhancer<?>> enhancers, String superClass) {
        this.scan = scan;
        this.enhancers = enhancers;
        this.superClass = superClass;
    }

    public static class Builder {
        private final Set<ClassEnhancer<?>> enhancers = ConcurrentHashMap.newKeySet();
        private String superClass;
        private ScanStrategy scan;

        public Builder(String... targetClassName) {
            this.scan = new ScanStrategy(Arrays.asList(targetClassName));
        }

        public Builder(StrClassName.Clazz... targetClassName) {
            List< String> clazzName = new ArrayList<>();
            for (StrClassName.Clazz clazz : targetClassName) {
                clazzName.add(clazz.getClassName());
            }
            this.scan = new ScanStrategy(clazzName);
        }

        public Builder superClass(String superClass) {
            this.superClass = superClass.replace('.', '/');
            return this;
        }

        public Builder exclude(String... excludeClass) {
            SimpleScan simpleScan = SimpleScan.moveToSimpleScan(scan);
            simpleScan.exclude(excludeClass);
            this.scan = simpleScan;
            return this;
        }

        public Builder classLoader(String classLoader) {
            scan.classLoader(classLoader);
            return this;
        }


        public Builder constantPoolScan(Predicate<byte[]> constantPoolScan) {
            this.scan = ConstantPool.moveToPoolClass(scan, constantPoolScan);
            return this;
        }

        public Builder injectVisit(IInjectMode iInjectVisit) {
            return forMethod("*", "*", iInjectVisit);
        }

        public Builder fieldSignature(FieldHandle... fieldSignature) {
            forMethod("*", "*", null,
                    new SimpleFieldSignatureEnhancer(fieldSignature));
            return this;
        }

        public Builder injectMethodStart(String methodName, Injects injectMethod) {
            InjectVisitMethod register = injectMethod
                    .function(methodName + "ASM", InjectPoint.INVOKE_BEFORE)
                    .register();
            return forMethod(methodName, "*", register);
        }

        public Builder injectMethodStart(String methodName, String targetDescriptor, Injects injectMethod) {
            InjectVisitMethod register = injectMethod
                    .function(methodName + "ASM", InjectPoint.INVOKE_BEFORE)
                    .register();
            return forMethod(methodName, targetDescriptor, register);
        }

        public Builder injectMethodReturn(String methodName, Injects injectMethod) {
            InjectVisitMethod register = injectMethod
                    .function(methodName + "ASM", InjectPoint.RETURN_BEFORE)
                    .register();
            return forMethod(methodName, "*", register);
        }


        public Builder forMethod(String methodName, IInjectMode iInjectVisit) {
            return forMethod(methodName, "*", iInjectVisit);
        }

        public Builder forMethod(String methodName, String targetDescriptor, IInjectMode injectMode) {
            if (injectMode instanceof InjectVisitCode) {
                return this.forMethod(methodName, targetDescriptor, injectMode, new SimpleVisitMethodEnhancer());
            } else {
                return this.forMethod(methodName, targetDescriptor, injectMode, new SimpleInjectClassEnhancer());
            }
        }

        @SuppressWarnings("unchecked")
        public Builder forMethod(String methodName, String targetDescriptor, IInjectMode injectMode, ClassEnhancer<?> classEnhancer) {
            TargetMethod targetMethod = new TargetMethod(methodName, targetDescriptor);
            for (ClassEnhancer<?> enhancer : enhancers) {
                if (enhancer.canMergeWith(classEnhancer)) {
                    ((ClassEnhancer<IInjectMode>) enhancer).addOrMerge(targetMethod, injectMode);
                    return this;
                }
            }
            ((ClassEnhancer<IInjectMode>) classEnhancer).addOrMerge(targetMethod, injectMode);

            enhancers.add(classEnhancer);
            return this;
        }


        public Builder already() {
            scan.already();
            return this;
        }

        public Builder createMethod(String methodName) {
            return this;
        }

        public Builder createConstructor(String constructorDescriptor, InjectInNewFunctionBase injectMethod) {
            return this.createConstructor(constructorDescriptor, null, 0, injectMethod);
        }

        public Builder createConstructor(String constructorDescriptor, int inheritMode, InjectInNewFunctionBase injectMethod) {
            return this.createConstructor(constructorDescriptor, null, inheritMode, injectMethod);
        }


        @SuppressWarnings("unchecked")
        public Builder createConstructor(String constructorDescriptor, String thisConstructorDescriptor, int inheritMode, InjectInNewFunctionBase injectMethod) {
            ConstructorEnhancer constructorEnhancer = new ConstructorEnhancer(constructorDescriptor, thisConstructorDescriptor, inheritMode);
            for (ClassEnhancer<?> enhancer : enhancers) {
                if (enhancer.canMergeWith(constructorEnhancer)) {
                    ((ClassEnhancer<InjectInNewFunctionBase>) enhancer).addOrMerge(null, injectMethod);
                    return this;
                }
            }
            constructorEnhancer.addOrMerge(null, injectMethod);
            enhancers.add(constructorEnhancer);
            return this;
        }


        public Builder classEnhancer(ClassEnhancer<?> classEnhancer) {
            enhancers.add(classEnhancer);
            return this;
        }


        public TransformerProvider build() {
            return new TransformerProvider(scan, enhancers, superClass);
        }
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!scan.scan(this, loader, className, classBeingRedefined, protectionDomain, classfileBuffer)) {
            return null;
        }
        String pluginName = null;
        if (scan.getClassLoader() != null) {
            if (loader.getClass().getName().equals(scan.getClassLoader())) {
                pluginName = loaderToPlugin.get(loader);
                if (pluginName == null) {
                    pluginName = extractPluginName(protectionDomain);
                    loaderToPlugin.put(loader, pluginName);
                    BridgeManager.LOADER_REGISTRY.put(pluginName, loader);
                }
            } else {
                return null;
            }
        }


        try {

         //   ClasspathAgent.dump(className + "Original", classfileBuffer);

            byte[] current = classfileBuffer;

            for (ClassEnhancer<?> enhancer : enhancers) {
                ClassReader classReader = new ClassReader(current);

                ClassWriter classWriter = enhancer.needFrame()
                        ? new ClassWriter(ClassWriter.COMPUTE_FRAMES)
                        : new ClassWriter(0);
                ClassVisitor visitor = enhancer.createVisitor(classWriter, this, pluginName, className, loader);
                classReader.accept(visitor, 0);
                current = classWriter.toByteArray();
            }


           // ClasspathAgent.dump(className, current);


            return current;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAlready(boolean already) {
        this.already = already;
    }

    public Set<String> getProcessed() {
        return processed;
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
