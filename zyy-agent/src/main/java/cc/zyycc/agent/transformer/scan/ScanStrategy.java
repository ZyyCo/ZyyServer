package cc.zyycc.agent.transformer.scan;


import cc.zyycc.agent.transformer.TransformerProvider;

import java.security.ProtectionDomain;
import java.util.List;
import java.util.function.Predicate;

public class ScanStrategy implements IScan {
    protected List<String> targetClassNames;
    protected Predicate<String> targetClassNamePredicate;
    protected String classLoader;
    protected boolean already;


    public ScanStrategy(List<String> targetClassNames) {
        this.targetClassNames = targetClassNames;
        targetClassNamePredicate = className -> false;
        for (String targetClassName : targetClassNames) {
            targetClassName = targetClassName.replace(".", "/");
            if (targetClassName.equals("*")) {
                targetClassNamePredicate = className -> true;
                break;
            }
            if (targetClassName.contains("*")) {
                String replace = targetClassName.replace("*", "");
                this.targetClassNamePredicate = targetClassNamePredicate.or(className -> className.startsWith(replace));
            } else {
                String finalTargetClassName = targetClassName;
                this.targetClassNamePredicate = targetClassNamePredicate.or(className -> className.equals(finalTargetClassName));
            }
        }
    }

    @Override
    public void classLoader(String classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean scan(TransformerProvider provider, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!targetClassNamePredicate.test(className)) {
            return false;
        }

        if (!already && classBeingRedefined != null && provider.getProcessed().contains(className)) {
            System.out.println("已处理过，不允许重复，本次跳过: " + className);
            return false;
        }
        return true;
    }


    @Override
    public ScanStrategy moveClass(ScanStrategy scan) {
        return scan.moveClass(this);
    }


    public void already() {
        this.already = true;
    }

    public String getClassLoader() {
        return classLoader;
    }
}
