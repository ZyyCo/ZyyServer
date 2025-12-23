package cc.zyycc.gradle.api;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public interface ClassEnhancer {
    String targetClassName();
    ClassVisitor createVisitor(ClassWriter cw);
}

