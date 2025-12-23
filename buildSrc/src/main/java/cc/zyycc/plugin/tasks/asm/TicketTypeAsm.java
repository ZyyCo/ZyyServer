package cc.zyycc.plugin.tasks.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class TicketTypeAsm implements ClassEnhancer{
    @Override
    public String targetClassName() {
        return null;
    }

    @Override
    public ClassVisitor createVisitor(ClassWriter cw) {
        return null;
    }
}
