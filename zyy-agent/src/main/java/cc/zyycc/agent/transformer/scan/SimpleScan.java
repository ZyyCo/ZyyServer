package cc.zyycc.agent.transformer.scan;


import java.util.List;
import java.util.function.Predicate;

public class SimpleScan extends ScanStrategy {
    private SimpleScan(List<String> targetClassNames) {
        super(targetClassNames);
    }

    public static SimpleScan moveToSimpleScan(ScanStrategy scan) {
        SimpleScan simpleScan = new SimpleScan(scan.targetClassNames);
        simpleScan.classLoader(scan.classLoader);
        simpleScan.already = scan.already;
        return simpleScan;
    }

    public void exclude(String... excludeClass) {
        Predicate<String> excludePredicate = className -> true;
        for (String cls : excludeClass) {
            String aClass = cls.replace('.', '/');
            if (aClass.endsWith("*")) {
                String replace = aClass.replace("*", "");
                excludePredicate = excludePredicate.and(className -> !className.startsWith(replace));
            } else {
                excludePredicate = excludePredicate.and(className -> !className.equals(aClass));
            }
        }
        this.targetClassNamePredicate = this.targetClassNamePredicate.and(excludePredicate);
    }
}
