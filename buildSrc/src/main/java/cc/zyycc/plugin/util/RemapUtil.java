package cc.zyycc.plugin.util;

import net.md_5.specialsource.JarMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RemapUtil {
    /**
     * 不带括号
     *
     * @return params
     */
    public static String parseParams(String params, Map<String, String> mappings) {
        if (params.length() < 3) {
            return params;
        }

        int start = 0;
        while (true) {
            int lnetIndex = params.indexOf("Lnet", start);
            if (lnetIndex == -1) break; // 没找到就结束
            // 找到后面的 ';'
            int semicolonIndex = params.indexOf(';', lnetIndex);
            //提取
            String className = params.substring(lnetIndex + 1, semicolonIndex);
            String fClass = mappings.get(className);
            if (fClass != null) {
                params = params.replace(className, fClass);
            }
            start = lnetIndex + 1;
        }
        return params;
    }

    /**
     * 解析descriptor
     *
     * @return params
     */

    public static String parseDesc(String desc, Map<String, String> mappings) {
        if (desc == null) {
            return null;
        }
        if (desc.length() < 6) {
            return desc;
        }
        if (desc.charAt(0) != '(') {
            return desc;
        }


        String oldParams = desc.substring(desc.indexOf('(') + 1, desc.indexOf(')'));
        String newParams = "(" + parseBofDesc(oldParams, mappings) + ")";
        String oldReturnType = desc.substring(desc.lastIndexOf(')') + 1);
        String newReturnType = parseBofDesc(oldReturnType, mappings);
        return newParams + newReturnType;
    }


    public static Map<MethodEntry, String> initMethodData(JarMapping mapping) {
        Map<MethodEntry, String> methodData = new HashMap<>();
        mapping.methods.forEach((key, value) -> {
            String[] split = key.split(" ");

            String className = split[0].substring(0, split[0].lastIndexOf('/'));
            String confusedMethodName = split[0].substring(split[0].lastIndexOf('/') + 1);
            String desc = split[1];
            methodData.put(new MethodEntry(className, confusedMethodName, desc), value);
        });
        return methodData;
    }

    public static Map<MethodEntry, String> initMcpData(JarMapping mapping) {
        Map<MethodEntry, String> methodData = new HashMap<>();
        //key:net/minecraft/world/Dimension/getDimensionType ()Lnet/minecraft/world/DimensionType;value:func_236063_b_
        mapping.methods.forEach((key, value) -> {
            String[] split = key.split(" ");


            String className = split[0].substring(0, split[0].lastIndexOf('/'));
            String forgeMethodName = split[0].substring(split[0].lastIndexOf('/') + 1);
            String desc = split[1];
            methodData.put(new MethodEntry(className, value, desc), forgeMethodName);
        });
        return methodData;
    }

    // bukkit key=net/minecraft/server/v1_16_R3/IMinecraftServer/getVersion ()Ljava/lang/String; value=H


//    public static String parseParametersDescriptor(String confusedReturnType, JarMapping mapping) {
//        String[] split = confusedReturnType.split("\\)");
//        //(Lod;Lcd;)Ljava/lang/String;
//        //(Lod;Lcd;
//        String parameters = split[0].substring(1);
//        //Ljava/lang/String;
//        String returnType = split[1];
//        if (parameters.contains(";")) {
//            String confusedParameterParameters = parameters.replace("(", "")
//                    .replaceAll("\\).*", "");
//            parameters = RemapUtil.parseParameters(confusedParameterParameters, mapping);
//        }
//        if (returnType.contains(";")) {
//            returnType = RemapUtil.parseParameters(returnType, mapping);
//        }
//
//        return "(" + parameters + ")" + returnType;
//    }


    public static String parseBofDesc(String params, Map<String, String> mappings) {
        //传进来的格式 Lsb;Ldsb;Z

        StringBuilder sb = new StringBuilder();
        for (String parameter : params.split(";")) {
            boolean search = false;
            String prefix = "";
//            if (parameter.startsWith("I")) {
//                System.out.println("123123211");
//            }
            //数组
            while (parameter.startsWith("[")) {
                prefix += "[";
                parameter = parameter.substring(1);
            }
            if (parameter.startsWith("Ljava/")) {
                sb.append(prefix).append(parameter).append(";");
                continue;
            }
            if (!parameter.startsWith("L")) {
                //UPPERCASE_LETTER
                //ZLaqa IIILcra
                char[] charArray = parameter.toCharArray();
                for (int i = 0; i < charArray.length; i++) {
                    if (charArray[i] == 'L') {
                        prefix = parameter.substring(0, i);
                        parameter = parameter.substring(i);
                        if (Character.isLowerCase(parameter.charAt(1))) {
                            search = true;
                            break;
                        }
                    }
                }
                if (!search) {
                    sb.append(prefix).append(parameter);
                    continue;
                }
            }

            //去掉L搜索反混淆类名
            String mapped = getMappingClassName(parameter.substring(1), mappings);
            if (mapped != null) {
                sb.append(prefix).append("L").append(mapped).append(";");
            } else {
                sb.append(prefix).append(parameter).append(";");
            }

        }

        return sb.toString();
    }


    public static String getMappingClassName(String confusedClass, Map<String, String> mappings) {
        String className = mappings.get(confusedClass);
        if (className == null && confusedClass.contains("$")) {
            className = confusedClass.substring(0, confusedClass.lastIndexOf("$"));
            className = mappings.get(className);
            if (className != null) {
                className = className + "$" + confusedClass.substring(confusedClass.lastIndexOf("$") + 1);
            }
        }
        return className;
    }


    public static boolean validateDescriptor(String input) {
        try {
            // 调用 transform 模拟解析，不实际 remap
            transform(input);
            return true; // ✅ 没出错代表合法
        } catch (IllegalArgumentException e) {
            System.err.println("❌ [非法描述符] " + input + " -> " + e.getMessage());
            return false;
        }
    }

    private static void transform(String input) {
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);

            switch (c) {
                // 对象类型，如 Ljava/lang/String;
                case 'L': {
                    int end = input.indexOf(';', i);
                    if (end == -1) {
                        throw new IllegalArgumentException("缺少 ';' 结束符");
                    }
                    String className = input.substring(i + 1, end);
                    if (className.isEmpty()) {
                        throw new IllegalArgumentException("L 后类名为空");
                    }
                    i = end; // 跳到分号处
                    break;
                }

                // 基础类型（byte, char, double, float, int, long, short, void, boolean）
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'S':
                case 'V':
                case 'Z':
                case '(':
                case ')':
                case '[': // 数组
                    break;

                default:
                    // 任何其他字符都是非法的（例如孤立的 ';'）
                    throw new IllegalArgumentException("无法识别的类型: " + c);
            }

            i++;
        }
    }


}
