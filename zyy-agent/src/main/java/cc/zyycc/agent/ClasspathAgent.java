package cc.zyycc.agent;


import cc.zyycc.agent.inject.*;
import cc.zyycc.agent.inject.hookResult.IInjectResult;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunction;
import cc.zyycc.agent.inject.method.InjectPoint;
import cc.zyycc.agent.inject.returnType.ReturnType;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.visitCode.*;
import cc.zyycc.agent.plugin.scan.SuperClassPreloader;
import cc.zyycc.agent.transformer.FieldSignatureTransformer;
import cc.zyycc.agent.transformer.NmsRemapTransformer;
import cc.zyycc.agent.transformer.TransformerProvider;
import cc.zyycc.agent.util.FieldNameHandle;
import cc.zyycc.agent.util.FieldDescHandle;
import cc.zyycc.common.VersionInfo;
import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.util.StrClassName;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class ClasspathAgent {
    public static final Map<String, Consumer<MethodVisitor>> map = new ConcurrentHashMap<>();

    public static final String BK_NMS_CONSTRUCTOR = "(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;Lnet/minecraft/world/chunk/listener/IChunkStatusListener;Lnet/minecraft/world/gen/ChunkGenerator;ZJLjava/util/List;ZLorg/bukkit/World$Environment;Lorg/bukkit/generator/ChunkGenerator;)V";

    public static final String NMS_CONSTRUCTOR = "(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;Lnet/minecraft/world/chunk/listener/IChunkStatusListener;Lnet/minecraft/world/gen/ChunkGenerator;ZJLjava/util/List;Z)V";


    public static void start(String args, Instrumentation inst) {
        System.out.println("Agent start");
        MappingManager.init();

        SuperClassPreloader.init();
        for (ModifyPermissionModifiers value : ModifyPermissionModifiers.values()) {
            inst.addTransformer(value.transformerProvider, true);
        }
        Injects fgASM = Injects.create("cc/zyycc/common/asm/forge/ServiceLoaderStreamUtilsAsm", "cc/zyycc/common/asm");

        InjectVisitMethod errorHandlingServiceLoaderASM = fgASM.function("errorHandlingServiceLoaderASM", InjectPoint.INVOKE_BEFORE)
                .args(0, 1).returnVisitMethod(1).register();
        TransformerProvider errorHandlingServiceLoader = new TransformerProvider.Builder("cpw/mods/modlauncher/ServiceLoaderStreamUtils")
                .forMethod("errorHandlingServiceLoader",
                        "(Ljava/lang/Class;Ljava/lang/ClassLoader;Ljava/util/function/Consumer;)Ljava/util/ServiceLoader;"
                        , errorHandlingServiceLoaderASM)
                .build();
        inst.addTransformer(errorHandlingServiceLoader, true);


        inst.addTransformer(new TransformerProvider.Builder("net/minecraftforge/fml/loading/moddiscovery/ModDiscoverer$LocatorClassLoader")
                .forMethod("<init>", new ModDiscoverer$LocatorClassLoaderCode())
                .build());


        inst.addTransformer(new TransformerProvider.Builder("cpw/mods/modlauncher/TransformingClassLoader")
                .already()
                .forMethod("<init>", new TransformingClassLoaderCode())
                .build());
        StrClassName.Clazz chunk = StrClassName.getStrClass("Chunk");
        inst.addTransformer(new TransformerProvider.Builder("org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftChunk")
                .fieldSignature(
                        new FieldNameHandle(chunk.toString(), chunk.getConfusionField("world"), "serverWorld"),
                        new FieldDescHandle("[Ljava/util/List;", "[Lnet/minecraft/util/ClassInheritanceMultiMap;", true))//owner
                .build());


        inst.addTransformer(new TransformerProvider.Builder(
                "org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftWorld",
                "org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftServer")
                .already()
                .forMethod("*", new CraftServerVisit())
                .build());


        //反射
        FieldSignatureTransformer aaaTransformer = new FieldSignatureTransformer();
        inst.addTransformer(aaaTransformer, true);


        StrClassName.Clazz commands = StrClassName.getStrClass("Commands");

        String dispatcherField = commands.getConfusionField("dispatcher");
        Injects commandsASM = Injects.create("cc/zyycc/bk/asm/mc/CommandAsm", "cc/zyycc/bk/asm/util");
        //field_197062_b
        InjectInNewFunction dispatcherInject = commandsASM.inNewFunction("getConstructor")
                .captureField(dispatcherField)
                .returnType(dispatcherField, "Lcom/mojang/brigadier/CommandDispatcher;")
                .register();
        InjectVisitMethod injectDispatch = commandsASM.function("handleCommandAsm")
                .cancelableHookResult()
                .targetField(InjectPoint.INVOKE_BEFORE, dispatcherField, "Lcom/mojang/brigadier/CommandDispatcher;", 2)
                .args(0, 1, 2)
                .register();

        TransformerProvider field197062B = new TransformerProvider.Builder(commands)
                .already()
                .forMethod(commands.getConfusionMethod("handleCommand"), "*", injectDispatch)
                .createConstructor("()V", dispatcherInject)
                .build();

        inst.addTransformer(field197062B, true);



        TransformerProvider serverWorldConstructor = new TransformerProvider.Builder(StrClassName.getStrClassName("ServerWorld"))
                .already()
                .createConstructor(BK_NMS_CONSTRUCTOR, NMS_CONSTRUCTOR, 1, new ServerWorldConstructor())
                .build();
        inst.addTransformer(serverWorldConstructor, true);


        InjectVisitMethod nettySystemInjectAsm = Injects.create("cc/zyycc/bk/asm/mc/network/NetWorkSystemAsm")
                .function("guard", InjectPoint.RETURN_BEFORE)
                .args(1)
                .register();

        TransformerProvider nettySystem = new TransformerProvider.Builder(StrClassName.getStrClassName("NetworkSystem$1"))
                .already()
                .forMethod("initChannel", nettySystemInjectAsm)
                .build();
        inst.addTransformer(nettySystem, true);


//
        pluginsEnhance(inst);
        inst.addTransformer(new NmsRemapTransformer(), true);
        StrClassName.clear();
    }

    public static void pluginsEnhance(Instrumentation inst) {


        InjectVisitMethod javaPluginAsm = Injects.create("cc/zyycc/bk/asm/bk/gcore/ReflectionMethodAsm")
                .function("reflectionMethodEnhancer")
                .targetVisitInsn(Opcodes.DUP, 2, InjectPoint.INVOKE_AFTER)
                .replaceLocal(2, ConditionReturn.STRING)
                .args(1, 2, 3)
                .register();

        TransformerProvider questInject = new TransformerProvider.Builder("com/guillaumevdn/gcore/lib/reflection/ReflectionMethod")
                .already()
                .classLoader("org.bukkit.plugin.java.PluginClassLoader")
                .forMethod("<init>", javaPluginAsm)
                .build();
        inst.addTransformer(questInject, true);


        //cmi
        InjectVisitMethod cmiReflectionInject = Injects.create("cc/zyycc/bk/asm/bk/cmi/ReflectionsAsm")
                .function("reflectionMethodEnhancer", InjectPoint.INVOKE_BEFORE)
                .args(1, 2, 3)
                .replaceLocal(1, ConditionReturn.STRING)
                .register();

        TransformerProvider cmiReflection = new TransformerProvider.Builder("net/Zrips/CMILib/Reflections")
                .already()
                .classLoader("org.bukkit.plugin.java.PluginClassLoader")
                .forMethod("getMethod", cmiReflectionInject)
                .build();
        inst.addTransformer(cmiReflection, true);


        InjectVisitMethod protocolInjectorInject = Injects.create("cc/zyycc/bk/asm/mc/network/NetWorkSystemAsm")
                .function("isLoginPhase")
                .targetField(InjectPoint.INVOKE_BEFORE, "closed", "Z", 1)
                .argFieldName("networkManager")
                .customInjectResult(new IInjectResult() {
                    @Override
                    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
                        Label skipReturn = new Label();
                        mv.visitJumpInsn(Opcodes.IFEQ, skipReturn);
                        mv.visitInsn(ConditionReturn.BOOLEAN_FALSE.getIconst());
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitInsn(Opcodes.MONITOREXIT);
                        mv.visitInsn(Opcodes.IRETURN);
                        mv.visitLabel(skipReturn);
                        return resultIndex;
                    }

                    @Override
                    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
                        return new ReturnType("Z");
                    }

                    @Override
                    public boolean needFrame() {
                        return true;
                    }
                })
                .register();
        //networkManager

        TransformerProvider protocolInjector = new TransformerProvider.Builder("com/comphenix/protocol/injector/netty/ChannelInjector")
                .already()
                .forMethod("inject", protocolInjectorInject)
                .build();
        inst.addTransformer(protocolInjector, true);
    }


    public static void dump(String className, byte[] bytes) {
        try {
            Path outDir = Paths.get("agent"); // 输出目录
            Files.createDirectories(outDir);

            Path outFile = outDir.resolve(className.replace('.', '/') + ".class");
            Files.createDirectories(outFile.getParent());

            try (FileOutputStream fos = new FileOutputStream(outFile.toFile())) {
                fos.write(bytes);
            }

            //    System.out.println("Class dumped to: " + outFile.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
