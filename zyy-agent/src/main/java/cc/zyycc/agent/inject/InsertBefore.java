package cc.zyycc.agent.inject;


import org.objectweb.asm.MethodVisitor;

public interface InsertBefore {

    public void injectCode(MethodVisitor mv);

    public void injectCodeAfter();
}
