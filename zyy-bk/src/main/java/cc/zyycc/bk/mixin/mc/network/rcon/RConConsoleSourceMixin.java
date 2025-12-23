package cc.zyycc.bk.mixin.mc.network.rcon;

import cc.zyycc.bk.bridge.network.RConConsoleSourceBridge;
import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import net.minecraft.command.CommandSource;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RConConsoleSource.class)
public class RConConsoleSourceMixin implements RConConsoleSourceBridge {
    @Final
    @Shadow
    private MinecraftServer server;

    public CommandSender getBukkitSender(CommandSource wrapper) {
        MinecraftServerBridge server = (MinecraftServerBridge) this.server;
        return server.bridge$getRemoteConsole();
    }

    @Override
    public CommandSender bridge$getBukkitSender(CommandSource commandSource) {
        return getBukkitSender(commandSource);
    }
}
