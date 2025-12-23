package cc.zyycc.agent.plugin;

import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PluginMethodRemapper extends MethodRemapper {


    protected PluginMethodRemapper(int api, MethodVisitor methodVisitor, Remapper remapper) {
        super(api, methodVisitor, remapper);
    }


    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (opcode == Opcodes.GETFIELD &&
                name.equals("trackedEntities") &&
                descriptor.equals("Lorg/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/ints/Int2ObjectMap;")) {

            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cc/zyycc/bk/bridge/world/server/ChunkManagerBridge",
                    "getTrackedEntities",
                    "(Lnet/minecraft/world/server/ChunkManager;)Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
                    false
            );
            return;
        }

        super.visitFieldInsn(opcode, owner, name, descriptor);
    }


    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        if (name.equals("getServer")
                && owner.endsWith("WorldServer")
        ) {
            super.visitInsn(Opcodes.POP);
            mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "cc/zyycc/bk/bridge/server/SimpleBridge",
                    "craftServer",
                    "Lorg/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftServer;"
            );
            return;
        }
        // aload_0
        // getfield #55 <com/onarandombox/MultiverseCore/utils/WorldManager.plugin : Lcom/onarandombox/MultiverseCore/MultiverseCore;>
        // invokevirtual #221 <com/onarandombox/MultiverseCore/MultiverseCore.getServer : ()Lorg/bukkit/Server;>
        // invokeinterface #226 <org/bukkit/Server.getWorldContainer : ()Ljava/io/File;> count 1
        //  aload_3
        //  invokespecial #229 <java/io/File.<init> : (Ljava/io/File;Ljava/lang/String;)V>
        if (opcodeAndSource == Opcodes.INVOKESPECIAL
                && owner.equals("java/io/File")
                && name.equals("<init>")) {
            if (descriptor.equals("(Ljava/io/File;Ljava/lang/String;)V")) {
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "cc/zyycc/bk/util/BKWorldFileBridge",
                        "resolveFile",
                        "(Ljava/io/File;Ljava/lang/String;)Ljava/lang/String;",
                        false);
                super.visitMethodInsn(Opcodes.INVOKESPECIAL,
                        "java/io/File",
                        "<init>",
                        "(Ljava/lang/String;)V",
                        false);
                return;
            }
        }

        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);

//        if (api < Opcodes.ASM5 && (opcodeAndSource & Opcodes.SOURCE_DEPRECATED) == 0) {
//            // Redirect the call to the deprecated version of this method.
//            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
//            return;
//        }
//        super.visitMethodInsn(
//                opcodeAndSource,
//                remapper.mapType(owner),
//                ((PluginRemapper)remapper).mapMethodName(owner, name, descriptor,isInterface),
//                remapper.mapMethodDesc(descriptor),
//                isInterface);

    }


}
