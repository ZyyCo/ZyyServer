package cc.zyycc.agent.inject.visitCode;

import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class CraftServerVisit extends InjectVisitCode {

    @Override
    public Consumer<MethodVisitor> visitFieldInsn(int opcode, final String owner, String name, String descriptor, MyMethodVisitor context) {
        return mv -> {
            if (opcode == Opcodes.GETFIELD) {
                if (name.equals("field_219377_e")) {
                    mv.visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            owner,
                            "bridge$getTickets",
                            "()Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;",
                            false);
                    return;
                } else if (name.equals("field_219252_f")) {
                    mv.visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            owner,
                            "bridge$getImmutableLoadedChunks",
                            "()Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;",
                            false);
                    return;
                } else if (name.equals("field_217498_x")) {
                    mv.visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            owner,
                            "bridge$getEntitiesById",
                            "()Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",
                            false);
                    return;
                }
            }
            if (descriptor.equals("Lnet/minecraft/world/storage/ServerWorldInfo;")) {
                mv.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        owner,
                        "bridge$getServerWorldInfo",
                        "()Lnet/minecraft/world/storage/ServerWorldInfo;",//你最喜欢的
                        false);
                return;
            }

            String newDesc = descriptor;
            String newOwner = owner;
            if (descriptor.contains("Lorg/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
                newDesc = descriptor.replace("org/bukkit/craftbukkit/libs/", "");
            } else if (owner.startsWith("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
                newOwner = owner.replace("org/bukkit/craftbukkit/libs/", "");
            }
            mv.visitFieldInsn(opcode, newOwner, name, newDesc);
        };

    }


    @Override
    public Consumer<MethodVisitor> visitMethodInsn(int opcode, String owner, final String name, String descriptor, boolean isInterface, MyMethodVisitor context) {
        return mv -> {
//            if (name.equals("<init>") && owner.equals("net/minecraft/world/server/ServerWorld")
//                    && descriptor.equals(createWorldDesc)) {
//                mv.visitInsn(Opcodes.POP); // POP gen
//                mv.visitInsn(Opcodes.POP); // POP env
//                mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
//                        owner, "<init>", newWorldDesc, false);
//                mv.visitInsn(Opcodes.DUP);//复制“刚new完的对象”
//                mv.visitVarInsn(Opcodes.ALOAD, 3);//gen
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "net/minecraft/world/server/ServerWorld",
//                        "bridge$setGenerator", "(Lorg/bukkit/generator/ChunkGenerator;)V", false);
//                mv.visitInsn(Opcodes.DUP);
//                mv.visitVarInsn(Opcodes.ALOAD, 1);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "org/bukkit/WorldCreator", "environment", "()Lorg/bukkit/World$Environment;", false);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "net/minecraft/world/server/ServerWorld",
//                        "bridge$setEnvironment", "(Lorg/bukkit/World$Environment;)V", false);
//
//                mv.visitInsn(Opcodes.DUP);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "net/minecraft/world/server/ServerWorld",
//                        "bridge$getWorld", "()Lorg/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftWorld;", false);
//                mv.visitInsn(Opcodes.POP);//弹出返回值
//                return;
//            }



            if (name.equals("func_76065_j")) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, "org/bukkit/craftbukkit/v1_16_R3/CraftWorld", "world", "Lnet/minecraft/world/server/ServerWorld;");
                mv.visitMethodInsn(opcode, owner, "bridge$getBKWorldName", "(Lnet/minecraft/world/World;)Ljava/lang/String;", isInterface);
                context.addMaxStack(1);
                return;
            }
            String newDesc = descriptor;
            String newOwner = owner;


            if (owner.startsWith("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
                newOwner = owner.replace("org/bukkit/craftbukkit/libs/", "");
            }
            if (descriptor.contains("Lorg/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
                newDesc = descriptor.replace("org/bukkit/craftbukkit/libs/", "");
            }
            mv.visitMethodInsn(opcode, newOwner, name, newDesc, isInterface);
        };
    }

    @Override
    public Consumer<MethodVisitor> visitTypeInsn(int opcode, final String type) {
        return mv -> {
            String newType = type;
            if (type.startsWith("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
                newType = type.replace("org/bukkit/craftbukkit/libs/", "");
            }
            mv.visitTypeInsn(opcode, newType);
        };
    }

    @Override
    public boolean needFrame() {
        return false;
    }
}




