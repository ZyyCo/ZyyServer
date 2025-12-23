package cc.zyycc.remap;

import cc.zyycc.remap.method.MappingManager;
import cc.zyycc.remap.method.MethodMappingEntry;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MappingTransformer;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CustomJarMapping extends JarMapping {
    public static final Map<MethodMappingEntry, MethodMappingEntry> methodEntry = new HashMap<>();

    public static final Map<String, String> convertedClass = new HashMap<>();
    private static final List<String> otherLines = new ArrayList<>();


    public void loadMappingsInEntry(InputStream in) throws IOException {
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
            loadFieldName(line);


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

    private void loadFieldName(String line) {
        if (line.indexOf('(') == -1) {
            int s1 = line.indexOf(' ');
            int s2 = line.indexOf(' ', s1 + 1);
            if (s2 != -1 && s1 != -1) {
                String className = line.substring(0, s1);
                String fieldName = line.substring(s1 + 1, s2);
                MappingManager.bkNMSField.add(fieldName);

                MappingManager.bkNMSFieldMapTable
                        .computeIfAbsent(className, k -> ConcurrentHashMap.newKeySet())
                        .add(fieldName);

            }
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
            MethodMappingEntry leftMethodEntry = createMethodEntry(leftClass, leftDesc, true);
            if (s3 != -1) {
                String rightClass = line.substring(s2 + 1, s3);
                String rightDesc = line.substring(s3 + 1);

                MethodMappingEntry rightEntry = createMethodEntry(rightClass, rightDesc, false);
                MappingManager.bkNMSMethodNameTable.put(leftMethodEntry.getMethodName(), rightEntry.getMethodName());
                return new MappingHandle(leftMethodEntry, rightEntry);
            } else {
                String rightMethodName = line.substring(s2 + 1);

                return new MappingHandle(leftMethodEntry, createValueEntry(leftClass, rightMethodName, leftDesc));
            }
        }
        return MappingHandle.empty();
    }

    private static MethodMappingEntry createMethodEntry(String classMethodName, String desc, boolean left) {
        //net/minecraft/nbt/INBT/asString ()Ljava/lang/String;
        try {
            String className = classMethodName.substring(0, classMethodName.lastIndexOf("/"));

            String methodName = classMethodName.substring(classMethodName.lastIndexOf("/") + 1);

            String params = desc.substring(desc.lastIndexOf("(") + 1, desc.lastIndexOf(")"));
            String returnType = desc.substring(desc.lastIndexOf(")") + 1);
            MethodMappingEntry entry = MethodMappingEntry.createMethodMappingEntry(className, methodName, params, returnType, desc);
            if (left) {
                MappingManager.bkNMSMethod.add(methodName + '#' + entry.getMethodDesc());
                MappingManager.bkNMSMethodName.add(methodName);
            }

            return entry;
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


    public static MappingHandle load(String line) {
        int s1 = line.indexOf(' ');
        int s2 = line.indexOf(' ', s1 + 1);
        int s3 = line.indexOf(' ', s2 + 1);
        if (s1 != -1 && s2 != -1) {
            String leftClass = line.substring(0, s1);
            String leftField = line.substring(s1 + 1, s2);
            BaseEntry leftEntry = new BaseEntry(leftClass, leftField);
            if (s3 != -1) {
                String rightClass = line.substring(s2 + 1, s3);
                String rightField = line.substring(s3 + 1);
                return new MappingHandle(leftEntry, new BaseEntry(rightClass, rightField));
            }
        }
        return MappingHandle.empty();
    }

}
