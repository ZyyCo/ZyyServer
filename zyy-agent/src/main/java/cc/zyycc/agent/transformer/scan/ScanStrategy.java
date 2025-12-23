package cc.zyycc.agent.transformer.scan;


import java.util.function.Predicate;

public class BaseScan implements IScan {
    protected final String targetClassName;
    protected Predicate<String> targetClassNamePredicate;
    protected String classLoader;
    public BaseScan(String targetClassName) {
        this.targetClassName = targetClassName.replace('.', '/');
        if (this.targetClassName.contains("*")) {
            this.targetClassNamePredicate = className -> {
                if (this.targetClassName.length() > 1) {
                    String replace = this.targetClassName.replace("*", "");
                    return className.startsWith(replace);
                }
                return true;
            };
        } else {
            this.targetClassNamePredicate = className -> className.equals(this.targetClassName);
        }
    }

    @Override
    public void classLoader(String classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean scan(ClassLoader loader, String className, byte[] classfileBuffer) {
        return false;
    }

    @Override
    public BaseScan moveClass(BaseScan scan) {
        return scan.moveClass(this);
    }


}
