package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.inject.visitCode.MyMethodVisitor;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class ModDiscovererCode extends InjectVisitCode {
    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        if (!(name.equals("add"))) {
            return mv -> mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
        return mv -> {

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


    @Override
    public boolean needFrame() {
        return false;
    }
}
