package cc.zyycc.plugin.tasks.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public interface ClassEnhancer {
    String targetClassName();
    ClassVisitor createVisitor(ClassWriter cw);
}

