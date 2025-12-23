package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public interface ClassEnhancer<C extends IInjectMode> {

    ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String targetClassName, ClassLoader classLoader);

    boolean enhancer();

    void addOrMerge(TargetMethod targetMethod, C c);

    Object identityKey();

    default boolean needFrame() {
        return false;
    }

    default boolean canMergeWith(ClassEnhancer<?> other) {
        return this.getClass() == other.getClass()
                && Objects.equals(this.identityKey(), other.identityKey());
    }



}
