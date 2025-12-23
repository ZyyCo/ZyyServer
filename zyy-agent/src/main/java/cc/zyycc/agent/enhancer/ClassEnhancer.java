package cc.zyycc.agent.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


public interface ClassEnhancer {
    ClassVisitor createVisitor(ClassWriter cw,TransformerProvider transformerProvider, String pluginName, String targetClassName, ClassLoader classLoader);

    boolean enhancer();
}
