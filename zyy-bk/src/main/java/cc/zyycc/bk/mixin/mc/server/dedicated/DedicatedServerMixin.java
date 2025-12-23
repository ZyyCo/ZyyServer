package cc.zyycc.bk.mixin.core.server.dedicated;

import cc.zyycc.bk.bridge.server.CraftServerBridge;
import cc.zyycc.bk.mixin.core.server.MinecraftServerMixin;
import cc.zyycc.common.bridge.BridgeHolder;
import joptsimple.OptionSet;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.v1_16_R3.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftRemoteConsoleCommandSender;
import org.bukkit.plugin.PluginLoadOrder;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.activation.CommandMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServerMixin {

    public OptionSet options = BridgeHolder.options;
    public int autosavePeriod;

    @Final
    @Shadow
    private RConConsoleSource rconConsoleSource;


    public DedicatedServerMixin() {

    }


    @Inject(method = "init", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/dedicated/DedicatedServer;setPlayerList(Lnet/minecraft/server/management/PlayerList;)V"))
    private void init(CallbackInfoReturnable<Boolean> cir) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        server = CraftServerBridge.craftServer;
        this.console = ColouredConsoleSender.getInstance();
        this.server.loadPlugins();
        this.server.enablePlugins(PluginLoadOrder.STARTUP);
        this.remoteConsole = new CraftRemoteConsoleCommandSender(this.rconConsoleSource);
    }


}
