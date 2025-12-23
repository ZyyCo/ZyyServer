package cc.zyycc.plugin.tasks.remap;

import cc.zyycc.plugin.ZyyPluginExtension;
import cc.zyycc.plugin.util.*;
import com.google.common.collect.Maps;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.transformer.MappingTransformer;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MappingsHandle {
    private final Project project;
    private final ZyyPluginExtension ext;

    private static final Map<String, String> confusedMethodMap = new HashMap<>();
    private static final Map<String, String> confusedFiledMap = new HashMap<>();
    private static final Map<String, String> fFiledMap = new HashMap<>();
    private static final Map<String, String> fMethodMap = new HashMap<>();

    public MappingsHandle(Project project, ZyyPluginExtension ext) {
        this.project = project;
        this.ext = ext;
    }

    public TaskProvider<Task> task(RegularFileProperty remapFileLazy, TaskProvider<Task> dependsOn) {

        return project.getTasks().register("remapSpigot", task -> {
            task.dependsOn(dependsOn);
            //tsrg
            task.doLast(tsrgLast -> {
                File tsrg = copyTsrg(remapFileLazy);
                File clFile = new File(ext.getBuildToolsDir(), "BuildData/mappings/bukkit-" + ext.getMinecraftVersion().get() + "-cl.csrg");
                File memberFile = new File(ext.getBuildToolsDir(), "BuildData/mappings/bukkit-" + ext.getMinecraftVersion().get() + "-members.csrg");


                File forgeSrg = new File(project.getTasks().getByName("extractSrg").getOutputs().getFiles().getAsPath());
                File forgeMcp = new File(project.getTasks().getByName("createMcpToSrg").getOutputs().getFiles().getAsPath());


                try {

                    String bkVersion = ext.getBukkitVersion().get();
                    JarMapping mapping = new JarMapping();
                    JarMapping forgeMapping = new JarMapping();
                    JarMapping forgeMcpMapping = new JarMapping();

                    forgeMapping.loadMappings(forgeSrg);
                    forgeMcpMapping.loadMappings(forgeMcp);

                    JarRemapperProvider jarRemapper = new JarRemapperProvider(mapping, new MappingMap() {
                        @Override
                        public String className(String className) {
                            if (className.startsWith("net/minecraft")) {

                                String clazz = className.substring(className.lastIndexOf('/') + 1);

                                return "net/minecraft/server/" + bkVersion + "/" + clazz;
                            }


                            if (Character.isLowerCase(className.charAt(0)) && !className.contains("/")) {
                                return className;
                            }
                            return className;
                        }
                    });

                    MappingTransformer transformer = jarRemapper.getMappingTransformer();

                    mapping.loadMappings(Files.newBufferedReader(clFile.toPath()), transformer, transformer, false);
                    mapping.loadMappings(Files.newBufferedReader(memberFile.toPath()), transformer, transformer, false);


                    mapping.classes.put("net/minecraft/server/MinecraftServer", "net/minecraft/server/" + bkVersion + "/MinecraftServer");

                    remapMethod(mapping, forgeMapping, forgeMcpMapping);
                    remapFiled(mapping, forgeMapping, forgeMcpMapping);


                    // zs$b net/minecraft/server/level/PlayerChunkMap$EntityTracker
                    //zs$a net/minecraft/world/server/ChunkManager$ProxyTicketManager
                    Map<String, String> remapClassCache = new HashMap<>();
                    for (Map.Entry<String, String> entry : forgeMapping.classes.entrySet()) {

                        String confusedClass = entry.getKey();
                        String forgeClass = entry.getValue();


                        //net/minecraft/server/level/PlayerChunkMap$EntityTracker
                        String bukkitClass = mapping.classes.get(confusedClass);
                        //zs$b zs$b
                        if (bukkitClass != null) {
                            //net/minecraft/server/level/PlayerChunkMap$EntityTracker net/minecraft/world/server/ChunkManager$ProxyTicketManager

                            remapClassCache.put(bukkitClass, forgeClass);
                        } else if (confusedClass.contains("$")) {//走到这里代表bukkit srg里没有反混淆,但forge有


                            String[] split = confusedClass.split("\\$");
                            //zs
                            String mainClass = split[0];
                            //b
                            String innerClass = split[1];
                            //zs$a
                            //zs$b
                            String bukkitMainClass = mapping.classes.get(mainClass);
                            if (bukkitMainClass != null) {
                                //net/minecraft/server/level/PlayerChunkMap 不带$
                                remapClassCache.put(bukkitMainClass + "$" + innerClass, forgeClass);
                            }
                        }

                    }
                    mapping.classes.clear();
                    mapping.classes.putAll(remapClassCache);

                    mapping.classes.put("net/minecraft/server/" + bkVersion + "/MinecraftServer", "net/minecraft/server/MinecraftServer");


                    File remapSrgWriter = new File(ext.getRemapFile().get().getAsFile().getParentFile(), "remap.srg");
                    //FD: net/minecraft/server/v1_16_R3/WorldServer/worldDataServer
                    // net/minecraft/world/server/ServerWorld/field_241103_E_

//                    mapping.fields.put("FD: net/minecraft/server/v1_16_R3/WorldServer/worldDataServer",
//                            "net/minecraft/world/server/ServerWorld/serverWorldInfo");
                    try (FileWriter writer = new FileWriter(remapSrgWriter)) {
                        writeMappings(writer, mapping.classes, "", false);
                        writeMappings(writer, mapping.fields, "", false);
                        writeMappings(writer, mapping.methods, "MD: ", false);
                    }
                    File forgeSrgWriter = new File(ext.getRemapFile().get().getAsFile().getParentFile(), "remapSrg.srg");

                    try (FileWriter writer = new FileWriter(forgeSrgWriter)) {
                        writeMappings(writer, mapping.classes, "", false);
                        writeMappings(writer, confusedFiledMap, "", false);
                        writeMappings(writer, confusedMethodMap, "MD: ", false);
                    }

                    File fFiledMapWriter = new File(ext.getRemapFile().get().getAsFile().getParentFile(), "confusion_fField.txt");
                    try (FileWriter writer = new FileWriter(fFiledMapWriter)) {
                        writeMappings(writer, fFiledMap, "", false);
                    }

                    File fMethodMapWriter = new File(ext.getRemapFile().get().getAsFile().getParentFile(), "confusion_fMethod.txt");
                    try (FileWriter writer = new FileWriter(fMethodMapWriter)) {
                        writeMappings(writer, fMethodMap, "", false);
                    }
//
//                    JarRemapper finalRemapper = new JarRemapper(mapping);
//
//                    finalRemapper.remapJar(Jar.init(ext.getSpigotFile()), remapFileLazy.get().getAsFile());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            });


        });
    }


    private static void remapMethod(JarMapping mapping, JarMapping forgeMapping, JarMapping forgeMCPMapping) {
        //bukkitkey=net/minecraft/server/v1_16_R3/MinecraftServer/H ()Ljava/lang/String;value=getVersion
        //forgekey=net/minecraft/client/MinecraftGame/getVersion ()Lcom/mojang/bridge/game/GameVersion;value=getVersion
        //key=net/minecraft/SharedConstants/a ()Lcom/mojang/bridge/game/GameVersion; value=getGameVersion
        //forge格式 key：vy/H ()Ljava/lang/String;value=func_71249_w
        //              od/a (Lod;)Ljava/lang/String;value=func_240746_a_

        //net/minecraft/nbt/ListNBT/
        //key:mj/add (ILjava/lang/Object;)V value=add


        //(Ljava/lang/String;Lnet/minecraft/server/v1_16_R3/NBTBase;)Lnet/minecraft/server/v1_16_R3/NBTBase;
        //net/minecraft/nbt/CompoundNBT/put
        // (Ljava/lang/String;Lnet/minecraft/server/v1_16_R3/NBTBase;)Lnet/minecraft/server/v1_16_R3/NBTBase;
        Map<String, String> remapCache = new HashMap<>();

        Map<MethodEntry, String> methodEntryStringMap = RemapUtil.initMethodData(mapping);

        Map<MethodEntry, String> mcpEntry = RemapUtil.initMcpData(forgeMCPMapping);
//key:net/minecraft/world/Dimension/getDimensionType ()Lnet/minecraft/world/DimensionType;
// value:func_236063_b_

        for (Map.Entry<String, String> entry : forgeMapping.methods.entrySet()) {
            //	getWorlds ()Ljava/lang/Iterable; func_212370_w
            //key=vy/H ()Ljava/lang/String;value=func_71249_w
            String key = entry.getKey();
            String forgeConfusedMethod = entry.getValue();
            String[] split = key.split(" ");
            //net/minecraft/server/v1_16_R3/MinecraftServer/H
            //vy
            String confusedClass = split[0].substring(0, split[0].lastIndexOf("/"));
            //H
            String confusedMethod = split[0].substring(split[0].lastIndexOf("/") + 1).trim();

            String confusedDesc = split[1];
            //net/minecraft/server/v1_16_R3/MinecraftServer

            String bukkitClassName = RemapUtil.getMappingClassName(confusedClass, mapping.classes);
            String forgeClassName = RemapUtil.getMappingClassName(confusedClass, forgeMapping.classes);

            if (bukkitClassName == null) {
                continue;
            }


            String bukkitDesc = RemapUtil.parseDesc(confusedDesc, mapping.classes);
            String forgeDesc = RemapUtil.parseDesc(confusedDesc, forgeMapping.classes);
            String forgeMethodName = mcpEntry.get(new MethodEntry(forgeClassName, forgeConfusedMethod, forgeDesc));
            if (forgeMethodName == null) {
                forgeMethodName = forgeConfusedMethod;
            }
            //获取bukkit Method
            String bukkitMethodName = confusedMethod;
            String s = methodEntryStringMap.get(new MethodEntry(bukkitClassName, confusedMethod, bukkitDesc));
            if (s != null) {
                bukkitMethodName = s;
            }
            String left = bukkitClassName + "/" + bukkitMethodName + " " + bukkitDesc;

            String right = forgeClassName + "/" + forgeMethodName + " " + forgeDesc;

            remapCache.put(left, right);

            confusedMethodMap.put(left,
                    forgeClassName + "/" + forgeConfusedMethod + " " + forgeDesc);

            fMethodMap.put(forgeClassName + "#" + forgeMethodName, forgeConfusedMethod);
        }

        mapping.methods.clear();
        mapping.methods.putAll(remapCache);

    }


    private static void remapFiled(JarMapping mapping, JarMapping forgeMapping, JarMapping forgeMCPMapping) {
        Map<String, String> fieldsCache = new HashMap<>();
        //key=net/minecraft/server/v1_16_R3/Enchantments/m value=DAMAGE_ALL
        //zy/e value=field_219378_f
        Map<FiledEntry, String> filedEntryMap = new HashMap<>();
        for (Map.Entry<String, String> entry : forgeMCPMapping.fields.entrySet()) {
            String[] split = entry.getKey().split(" ");
            String className = split[0].substring(0, split[0].lastIndexOf("/"));
            String filedName = split[0].substring(split[0].lastIndexOf("/") + 1).trim();
//            if(entry.getValue().equals("field_219378_f")){
//                System.out.println(entry.getValue());
//            }
            filedEntryMap.put(new FiledEntry(className, entry.getValue()), filedName);
        }


        for (Map.Entry<String, String> entry : forgeMapping.fields.entrySet()) {

            String key = entry.getKey();

            //zy
            String confusedClassName = key.substring(0, key.lastIndexOf("/"));
            //e
            String fieldName = key.substring(key.lastIndexOf("/") + 1);
            String bukkitClassName = mapping.classes.get(confusedClassName);
            String forgeClassName = forgeMapping.classes.get(confusedClassName);

            if (bukkitClassName == null) {
//                bukkitClassName = confusedClassName;
                continue;
            }
            //net/minecraft/server/v1_16_R3/Enchantments/m
            String bkMapKey = bukkitClassName + "/" + fieldName;
            //DAMAGE_ALL
            String bkFiled = mapping.fields.get(bkMapKey);

            String forgeFiled = filedEntryMap.get(new FiledEntry(forgeClassName, entry.getValue()));
            //net/minecraft/server/v1_16_R3/Enchantments DAMAGE_ALL SHARPNESS

            if (forgeFiled == null) {
                continue;
            }
            if (bkFiled != null) {
                fieldsCache.put(bukkitClassName + " " + bkFiled, forgeFiled);
                confusedFiledMap.put(bukkitClassName + " " + bkFiled, entry.getValue());
            } else {
                fieldsCache.put(bukkitClassName + " " + fieldName, forgeFiled);
                confusedFiledMap.put(bukkitClassName + " " + fieldName, entry.getValue());
            }
            fFiledMap.put(forgeClassName + "#" + forgeFiled, entry.getValue());
        }
        mapping.fields.clear();
        mapping.fields.putAll(fieldsCache);
    }


//    private static void remapFiled(JarMapping mapping, JarMapping forgeMapping, Map<String, String> flatten) {
//        Map<String, String> fieldsCache = new HashMap<>();
//
//        for (Map.Entry<String, String> entry : mapping.fields.entrySet()) {
//            String key = entry.getKey();
//            //net/minecraft/enchantment/Enchantments
//            String packageName = key.substring(0, key.lastIndexOf("/"));
//            String methodName = key.substring(key.lastIndexOf('/') + 1);
//            //取出混淆类名
//            String className = mapping.classes.get(packageName);
//            //zo/b
//            String forgeMethodKey = className + "/" + entry.getValue();
//            //field_120040_a
//            String forgeMethodName = forgeMapping.fields.get(forgeMethodKey);
//
//            if (forgeMethodName != null) {
//                //net/minecraft/server/v1_16_R3/Enchantments DAMAGE_ALL SHARPNESS
//                String s = flatten.get(forgeMethodName).split(" ")[0];
//                fieldsCache.put(packageName + " " + methodName, s);
//            }
//        }
//        mapping.fields.clear();
//        mapping.fields.putAll(fieldsCache);
//    }

    private static void writeMappings(FileWriter craftbukkitPackageWriter, Map<String, String> map, String prefix, boolean reverse) throws IOException {

        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            String value = stringStringEntry.getValue();
            String key = stringStringEntry.getKey();
            if (reverse) {
                craftbukkitPackageWriter.write(prefix + value + " " + key + "\n");
            } else {
                craftbukkitPackageWriter.write(prefix + key + " " + value + "\n");
            }
        }
    }


    public File copyTsrg(RegularFileProperty remapFileLazy) {
        File file = new File(remapFileLazy.getAsFile().get().getParent(), "craftbukkit.tsrg");
        try (InputStream in = RenameSpigot.class.getClassLoader().getResourceAsStream("craftbukkit.tsrg")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    public void genClassName(Map<String, String> mapping) {

        Project commonProject = project.findProject(":zyy-common");
        Path generatedSrcDir = Paths.get(commonProject.getBuildDir().getPath(), "generated-src", "cc/zyycc/common");
        File className = generatedSrcDir.resolve("ClassStrName.java").toFile();
        try (FileWriter writer = new FileWriter(className)) {
            writer.write("package cc.zyycc.common;\n");
            writer.write("  public class ClassStrName {\n");
            mapping.values()
                    .stream().filter(clazzName -> clazzName.startsWith("net/minecraft/") && !clazzName.startsWith("net/minecraft/advancements") && !clazzName.startsWith("net/minecraft/entity/merchant/"))
                    .forEach(clazzName -> {
                        try {
                            writer.write("    public static final String "
                                    + clazzName.substring(clazzName.lastIndexOf("/") + 1)
                                    + " = \"" + clazzName + "\";" + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            writer.write("}");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
