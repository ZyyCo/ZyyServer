package cc.zyycc.common.mapper;

import cc.zyycc.common.mapper.method.MethodMappingEntry;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MappingTransformer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CustomJarMapping extends JarMapping {
    public static final Map<MethodMappingEntry, MethodMappingEntry> methodEntry = new HashMap<>();

    public static final Map<String, String> convertedClass = new HashMap<>();
    private static final List<String> otherLines = new ArrayList<>();

    public void loadMappingsInEntry(InputStream  in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            loadMappingsInEntry(reader, null, null, false);
        }
    }

    public void loadMappingsInEntry(BufferedReader reader, MappingTransformer inputTransformer, MappingTransformer outputTransformer, boolean reverse) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("MD: ")) {
                line = line.replace("MD: ", "");
            }
            MappingHandle methodMappingHandle = loadMethod(line);

            if (!methodMappingHandle.isEmpty()) {
                Consumer<MappingHandle> handle = entry ->
                        methodEntry.put((MethodMappingEntry) entry.getLeftEntry(), (MethodMappingEntry) entry.getRightEntry());
                methodMappingHandle.handle(handle);
            } else {
                otherLines.add(line);
            }
        }
        String joined = String.join("\n", otherLines);
        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(joined))) {
            super.loadMappings(bufferedReader, inputTransformer, outputTransformer, reverse);
        }
    }


    public static MappingHandle loadMethod(String line) {
        //net/minecraft/entity/LivingEntity/cP ()Z net/minecraft/entity/LivingEntity/func_230296_cM_      ()Z
        //net.minecraft.nbt.INBT.asString      ()Ljava/lang/String; net.minecraft.nbt.INBT.func_150285_a_ ()Ljava/lang/String;
        //net/minecraft/nbt/CompoundNBT/set (Ljava/lang/String;Lnet/minecraft/nbt/INBT;)Lnet/minecraft/nbt/INBT; func_218657_a
        int s1 = line.indexOf(' ');
        int s2 = line.indexOf(' ', s1 + 1);
        int s3 = line.indexOf(' ', s2 + 1);
        if (s1 != -1 && s2 != -1 && line.indexOf('(') != -1) {
            String leftClass = line.substring(0, s1);
            String leftDesc = line.substring(s1 + 1, s2);
            MethodMappingEntry leftMethodEntry = createMethodEntry(leftClass, leftDesc);
            if (s3 != -1) {
                String rightClass = line.substring(s2 + 1, s3);
                String rightDesc = line.substring(s3 + 1);
                return new MappingHandle(leftMethodEntry, createMethodEntry(rightClass, rightDesc));
            } else {
                String rightMethodName = line.substring(s2 + 1);
                return new MappingHandle(leftMethodEntry, createValueEntry(leftClass, rightMethodName, leftDesc));
            }
        }
        return MappingHandle.empty();
    }

    private static MethodMappingEntry createMethodEntry(String classMethodName, String desc) {
        //net/minecraft/nbt/INBT/asString ()Ljava/lang/String;
        try {
            String className = classMethodName.substring(0, classMethodName.lastIndexOf("/"));
            String methodName = classMethodName.substring(classMethodName.lastIndexOf("/") + 1);
            String params = desc.substring(desc.lastIndexOf("(") + 1, desc.lastIndexOf(")"));
            String returnType = desc.substring(desc.lastIndexOf(")") + 1);
            return MethodMappingEntry.createMethodMappingEntry(className, methodName, params, returnType, desc);
        } catch (Exception e) {
            throw new RuntimeException("文本解析错误: 没有找到 / 符号");
        }
    }

    private static MethodMappingEntry createValueEntry(String classMethodName, String methodName, String desc) {
        //net/minecraft/nbt/INBT/asString ()Ljava/lang/String;
        try {
            String className = classMethodName.substring(0, classMethodName.lastIndexOf("/"));
            String params = desc.substring(desc.lastIndexOf("(") + 1, desc.lastIndexOf(")"));
            String returnType = desc.substring(desc.lastIndexOf(")") + 1);
            return MethodMappingEntry.createMethodMappingEntry(className, methodName, params, returnType, desc);
        } catch (Exception e) {
            throw new RuntimeException("文本解析错误: 没有找到 / 符号");
        }
    }

}
