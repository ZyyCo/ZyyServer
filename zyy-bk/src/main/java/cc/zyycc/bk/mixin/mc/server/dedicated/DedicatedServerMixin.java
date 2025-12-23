package cc.zyycc.bk.mixin.mc.server.dedicated;

import cc.zyycc.bk.bridge.server.SimpleBridge;
import cc.zyycc.bk.mixin.mc.server.MinecraftServerMixin;
import io.netty.bootstrap.ServerBootstrap;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PendingCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftRemoteConsoleCommandSender;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.PluginLoadOrder;

import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServerMixin {

    public int autosavePeriod;

    @Final
    @Shadow
    private RConConsoleSource rconConsoleSource;




//    @Shadow
//    public abstract ServerProperties getServerProperties();

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;isServerInOnlineMode()Z"))
    private void init(CallbackInfoReturnable<Boolean> cir) {


    }


    @Inject(method = "init", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/dedicated/DedicatedServer;setPlayerList(Lnet/minecraft/server/management/PlayerList;)V"))
    private void init2(CallbackInfoReturnable<Boolean> cir) {
        server = SimpleBridge.craftServer;
        SpigotConfig.init((File) this.options.valueOf("spigot-settings"));
        SpigotConfig.registerCommands();
        this.console = ColouredConsoleSender.getInstance();
        this.server.loadPlugins();
        this.server.enablePlugins(PluginLoadOrder.STARTUP);
        this.remoteConsole = new CraftRemoteConsoleCommandSender(this.rconConsoleSource);
    }


    @Redirect(method = "executePendingCommands", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/Commands;handleCommand(Lnet/minecraft/command/CommandSource;Ljava/lang/String;)I"))
    private int executePendingCommands(Commands serverCommand, CommandSource source, String command) {
        if (command.isEmpty()) {
            return 0;
        }
        ServerCommandEvent event = new ServerCommandEvent(this.console, command);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.server.dispatchServerCommand(console, new PendingCommand(event.getCommand(), source));
        }
        return 0;
    }




}
