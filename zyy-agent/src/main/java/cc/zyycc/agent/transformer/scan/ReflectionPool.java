package cc.zyycc.agent.transformer.scan;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReflectionPool {
    private static final String CLS_CLASS = "java/lang/Class";
    private static final String CLS_METHOD = "java/lang/reflect/Method";
    private static final String CLS_FIELD = "java/lang/reflect/Field";
    private static final String CLS_CONSTRUCTOR = "java/lang/reflect/Constructor";

    private static final Set<String> REFLECTION_METHODS =
            new HashSet<>(Arrays.asList(
                    "forName",
                    "getMethod",
                    "getDeclaredMethod",
                    "getField",
                    "getDeclaredField",
                    "getConstructor",
                    "getDeclaredConstructor",
                    "invoke",
                    "get",
                    "set",
                    "newInstance"
            ));


    private static final byte[][] REFLECTION_SCANS = {
            "forName".getBytes(StandardCharsets.UTF_8),
            "loadClass".getBytes(StandardCharsets.UTF_8),
            "getMethod".getBytes(StandardCharsets.UTF_8),
            "getField".getBytes(StandardCharsets.UTF_8),
            "getDeclaredField".getBytes(StandardCharsets.UTF_8),
            "getDeclaredMethod".getBytes(StandardCharsets.UTF_8)
    };


    public static boolean containsReflection(byte[] classBytes) {
        for (byte[] reflectionScan : REFLECTION_SCANS) {
            if (contains(classBytes, reflectionScan)) {
                return true;
            }
        }
        return false;
    }


    private static boolean contains(byte[] data, byte[] pattern) {
        outer:
        for (int i = 0; i + pattern.length <= data.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

//    public static boolean containsReflection(byte[] classBytes) {
//        ClassReader cr = new ClassReader(classBytes);
//
//        int itemCount = cr.getItemCount();
//        char[] buf = new char[1024];
//
//        boolean foundClass = false;
//        boolean foundReflectionMethod = false;
//
//        for (int i = 1; i < itemCount; i++) {
//
//            int index = cr.getItem(i);
//            if (index == 0) continue;
//
//            int tag = cr.readByte(index - 1);
//
//            try {
//
//                // CONSTANT_Utf8：需要动态扩容
//                if (tag == 1) {
//                    int utfLen = cr.readUnsignedShort(index + 1);
//                    if (utfLen > buf.length) {
//                        buf = new char[utfLen];
//                    }
//
//                    String s = (String) cr.readConst(index, buf);
//                    if (s == null) continue;
//                    if (s.contains("forName") || s.contains("getMethod") || s.contains("getDeclared")) {
//                        return true;
//                    }
//                }
//
//                // Class (7) / String (8): 不读 length
//                else if (tag == 7 || tag == 8) {
//                    String s = (String) cr.readConst(index, buf);
//                    if (s == null) continue;
//                    if (s.contains("java/lang/Class") || s.contains("forName")) {
//                        return true;
//                    }
//                }
//
//            } catch (Throwable ignored) {
//                // 防止损坏 UTF8 崩溃
//            }
//        }
//        return false;
//    }


}
