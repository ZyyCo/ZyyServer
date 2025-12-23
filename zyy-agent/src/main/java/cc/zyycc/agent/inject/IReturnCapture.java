package cc.zyycc.agent.inject;

import org.objectweb.asm.MethodVisitor;

public interface IReturnCapture {

    void scanField(String name, String descriptor);

    void pushReplaceVar(MethodVisitor mv, int resultIndex,String desc);


    String getDesc();
}
