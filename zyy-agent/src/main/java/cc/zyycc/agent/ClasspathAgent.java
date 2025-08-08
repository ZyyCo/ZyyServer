package cc.zyycc.agent;

import cc.zyycc.agent.enhancer.AsmEnhancer;
import cc.zyycc.agent.enhancer.code.ModDiscoverer$LocatorClassLoaderCode;
import cc.zyycc.agent.enhancer.code.ErrorCode;
import cc.zyycc.agent.enhancer.code.ModDiscovererCode;
import cc.zyycc.agent.enhancer.code.TransformingClassLoaderCode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ClasspathAgent {


    public static void agentmain(String agentArgs, Instrumentation inst) {

        DeMethod errorMethod = new DeMethod("errorHandlingServiceLoader",
                "(Ljava/lang/Class;Ljava/lang/ClassLoader;Ljava/util/function/Consumer;)Ljava/util/ServiceLoader;",
                new ErrorCode());
        printClassSource(inst, "cpw.mods.modlauncher.ServiceLoaderStreamUtils", errorMethod);

        DeMethod modDiscovererCode = new DeMethod("<init>", new ModDiscovererCode());
        printClassSource(inst, "net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer", modDiscovererCode);


        DeMethod modDiscoverer$LocatorClassLoaderCode = new DeMethod("<init>", new ModDiscoverer$LocatorClassLoaderCode());
        printClassSource(inst, "net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader", modDiscoverer$LocatorClassLoaderCode);


        DeMethod transformingClassLoaderCode = new DeMethod("<init>", new TransformingClassLoaderCode());
        printClassSource(inst, "cpw/mods/modlauncher/TransformingClassLoader", transformingClassLoaderCode);


    }

    public static void printClassSource(Instrumentation inst, String tarClassName, DeMethod deMethod) {
        String targetClassName = tarClassName.replace('.', '/');
        inst.addTransformer(transformerWithSelfReference(inst, targetClassName, deMethod), true);
    }

    public static ClassFileTransformer transformerWithSelfReference(Instrumentation inst, String targetClassName, DeMethod deMethod) {

        AtomicReference<ClassFileTransformer> ref = new AtomicReference<>();
        AtomicReference<String> capturedClassName = new AtomicReference<>();
        Map<String, ClassLoader> loaderMap = new ConcurrentHashMap<>();
        AtomicReference<Boolean> isLoaded = new AtomicReference<>(false);
        ClassFileTransformer transformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {

            if (className.equals(targetClassName)) {

                try {
                    //存入map
                    String classNameDot = className.replace('/', '.');
                    loaderMap.put(classNameDot, loader);
                    capturedClassName.set(classNameDot);
                    //插入字节码
                    byte[] enhance = AsmEnhancer.enhance(classfileBuffer, deMethod);

                    return enhance;
                    //System.out.println("🔧 是否可修改: " + inst.isModifiableClass(classBeingRedefined));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    isLoaded.set(true);
                   // inst.removeTransformer(ref.get());
                }
            }
            return null;

        };

        ref.set(transformer);


        // 然后在 main 线程 / 定时器中异步执行：
        new Thread(() -> {
            while (!isLoaded.get()) {
                try {
                    Thread.sleep(500); // ⏳ 留足字节码注入时间
                    String className = capturedClassName.get();

                    if(className == null){
                        continue;
                    }

                    ClassLoader loader = loaderMap.get(className);
                    if (loader != null) {
                        Class<?> clazz = Class.forName(className, false, loader);
                        inst.retransformClasses(clazz);
                    } else {
                        loaderMap.remove(className);
                        System.err.println("⚠️ 等待加载类失败");
                    }

                } catch (Exception e) {
//                    System.err.println("❌ 获取类失败！");
                    e.printStackTrace();
                }
            }

        }).start();


        return transformer;
    }

    private static Object findSingleton(Class<?> clazz) throws Exception {
        // 一般是 getInstance / INSTANCE / instance 单例
        try {
            Method getInstance = clazz.getDeclaredMethod("getInstance");
            getInstance.setAccessible(true);
            return getInstance.invoke(null);
        } catch (NoSuchMethodException e) {
            // fallback：去找静态字段 INSTANCE
            for (Field f : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(clazz)) {
                    f.setAccessible(true);
                    return f.get(null);
                }
            }
        }
        return null;
    }

    public static Map<String, String> parseAgentArgs(String args) {
        Map<String, String> map = new HashMap<>();
        if (args != null) {
            for (String entry : args.split(";")) {
                String[] parts = entry.split("=", 2);
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return map;
    }

}
