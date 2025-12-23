package cc.zyycc.common.util;

public class Version {
    public static final int javaVersion = Integer.parseInt(System.getProperty("java.class.version").substring(0, 2));

    //-44
    public static boolean checkJavaVersion() {
        return javaVersion >= 52 && javaVersion <= 61;
    }



    public static boolean greaterThanJava8() {
        return javaVersion > 52 && javaVersion <= 61;
    }
}
