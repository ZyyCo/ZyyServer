package cc.zyycc.plugin.util;

import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;

import java.util.*;

public class MethodData {

    private final String bukkitClassName;

    public final Map<String, String> methodNameReturnType = new HashMap<>();
    private final String bukkitReturnType;

    private final String bukkitMethodName;


    private final String confusedMethodName;

    public static final Map<String, MethodData> sortBukkit = new HashMap<>();

    public final Set<String> bukkitReturnTypes = new HashSet<>();

    public MethodData(String bukkitClassName, String bukkitMethodName, String bukkitReturnType, String confusedMethodName) {
        this.bukkitClassName = bukkitClassName;
        this.bukkitMethodName = bukkitMethodName;
        this.bukkitReturnType = bukkitReturnType;
        this.confusedMethodName = confusedMethodName;
    }


    public String compareReturnType(String analysisForgeReturnType) {
        for (String bukkitReturnType : bukkitReturnTypes) {
            if (analysisForgeReturnType.equals(bukkitReturnType)) {
                return bukkitReturnType;
            }
        }



        return analysisForgeReturnType;
    }


//    public String compareReturnType(String analysisForgeReturnType) {
//        for (String returnType : this.bukkitReturnTypes) {
//            if (analysisForgeReturnType.equals(returnType)) {
//                return returnType;
//            }
//        }
//
//        return this.bukkitReturnType;
//    }

    //forge对应bukkit的反混淆是否存在
    public static MethodData getBukkitRemapData(String bukkitClassName, String confusedMethodName) {
        return sortBukkit.get(bukkitClassName + "/" + confusedMethodName);
    }
    //Descriptor 查找方法
    public String getBukkitMethodName(String bukkitReturnType) {
        if (methodNameReturnType.get(bukkitReturnType) == null) {
            return bukkitMethodName;
        }
        return methodNameReturnType.get(bukkitReturnType);
    }




}
