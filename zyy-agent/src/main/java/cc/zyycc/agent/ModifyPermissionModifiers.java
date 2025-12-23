package cc.zyycc.agent;

import cc.zyycc.agent.enhancer.ModifiersPerEnhancer;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.Opcodes;

public enum ModifyPermissionModifiers {


    TickType_FILED_PLUGIN("net.minecraft.world.server.TicketType", Type.FIELD, "PLUGIN"),
    TickType_FILED_PLUGIN_TICKET("net.minecraft.world.server.TicketType", Type.FIELD, "PLUGIN_TICKET"),
    MinecraftServer_METHOD_getServer("net.minecraft.server.MinecraftServer", Type.METHOD, "getServer"),
    METHOD_SkullTileEntity_b("net.minecraft.tileentity.SkullTileEntity", Type.METHOD, "b");

    public final TransformerProvider transformerProvider;

    ModifyPermissionModifiers(String targetClassName, int type, String... names) {
        transformerProvider = new TransformerProvider.Builder(targetClassName)
                .already()
                .classEnhancer(new ModifiersPerEnhancer(Opcodes.ACC_PUBLIC, type, names)).build();
    }


    public static class Type {
        public static final int METHOD = 1;
        public static final int FIELD = 0;
    }

}
