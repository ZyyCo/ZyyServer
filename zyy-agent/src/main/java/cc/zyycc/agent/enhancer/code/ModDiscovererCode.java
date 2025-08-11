package cc.zyycc.agent.enhancer.code;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class ModDiscovererCode implements IVisitCode {
    public static int line = 0;
    int state = 0;
    boolean patching = false;

    @Override
    public Consumer<MethodVisitor> code() {
        return null;
    }

    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!(
                name.equals("add"))
        ) {
            return mv -> mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
        return mv -> {


          //  mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);


            mv.visitVarInsn(Opcodes.ALOAD, 0);
            // 调用 Hook.saveDiscoverer(Object)
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cc/zyycc/forge/debug/AgentHook",
                    "saveDiscoverer",
                    "(Ljava/lang/Object;)V",
                    false
            );



        };
    }



}
