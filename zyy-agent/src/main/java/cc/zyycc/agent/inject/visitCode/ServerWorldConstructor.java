package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.agent.inject.InjectInfo;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunctionBase;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;


public class ServerWorldConstructor extends InjectInNewFunctionBase {

    public ServerWorldConstructor() {
        super(null);
    }

    @Override
    public boolean needFrame() {
        return false;
    }

    @Override
    public InjectInfo injectCode(MethodVisitor mv, InjectInfo info) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        List<Integer> slots = info.getSlotList();
        mv.visitVarInsn(Opcodes.ALOAD, slots.get(slots.size() - 1));//long关系。
        mv.visitFieldInsn(Opcodes.PUTFIELD, info.getClassName(), "generator", "Lorg/bukkit/generator/ChunkGenerator;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, slots.get(slots.size() - 2));
        mv.visitFieldInsn(Opcodes.PUTFIELD, info.getClassName(), "environment", "Lorg/bukkit/World$Environment;");




        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, info.getClassName(), "getWorld", "()Lorg/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftWorld;", false);

        info.setMaxInfo(info.getSlots().size(), info.getSlots().size());
        return info;
    }

    @Override
    public void injectPush(MethodVisitor mv, String className, String descriptor, int index) {
        if (descriptor.equals("Lnet/minecraft/world/storage/IServerWorldInfo;")) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/world/storage/ServerWorldInfo");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/storage/ServerWorldInfo", "bkCreate", "()Lnet/minecraft/world/storage/ServerWorldInfo;", false);
        }
    }
}
