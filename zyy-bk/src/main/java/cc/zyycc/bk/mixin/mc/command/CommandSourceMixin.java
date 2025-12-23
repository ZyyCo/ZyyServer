package cc.zyycc.bk.mixin.mc.command;

import cc.zyycc.bk.bridge.command.ICommandSourceBridge;
import cc.zyycc.bk.bridge.network.RConConsoleSourceBridge;
import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSource.class)
public class CommandSourceMixin {
    @Final
    @Shadow
    private ICommandSource source;


    public CommandSender getBukkitSender() {

        if (source instanceof MinecraftServer) {
            System.out.println("MinecraftServer");
        }

        if (source instanceof CommandBlockLogic) {
            return org.bukkit.Bukkit.getConsoleSender();
        }
        if (source instanceof RConConsoleSource) {
            System.out.println("RConConsoleSource");
        }


        return ((ICommandSourceBridge)source).bridge$getBukkitSender((CommandSource) (Object) this);
    }
}
