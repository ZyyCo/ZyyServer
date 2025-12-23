package cc.zyycc.agent.transformer.scan;

import cc.zyycc.agent.transformer.TransformerProvider;

import java.security.ProtectionDomain;
import java.util.List;
import java.util.function.Predicate;

public class ConstantPool extends ScanStrategy {


    private final Predicate<byte[]> predicate;

    private ConstantPool(List<String> targetClassNames, Predicate<byte[]> predicate) {
        super(targetClassNames);
        this.predicate = predicate;
    }

    public static ConstantPool moveToPoolClass(ScanStrategy scanStrategy, Predicate<byte[]> predicate) {
        ConstantPool constantPool = new ConstantPool(scanStrategy.targetClassNames, predicate);
        constantPool.classLoader(scanStrategy.classLoader);
        constantPool.already = scanStrategy.already;
        return constantPool;
    }


    @Override
    public boolean scan(TransformerProvider provider, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!targetClassNamePredicate.test(className)) {
            return false;
        }
        if (this.classLoader != null && !loader.getClass().getName().equals(classLoader)) {
            return false;
        }

        if (!predicate.test(classfileBuffer)) {
            return false;
        }
        return true;
    }


}
