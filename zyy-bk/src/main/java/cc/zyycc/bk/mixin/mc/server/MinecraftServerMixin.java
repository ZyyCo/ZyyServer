package cc.zyycc.bk.mixin.core.server;

import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.server.ChunkManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(MinecraftServer.class)

public abstract class MinecraftServerMixin implements MinecraftServerBridge {

    public CraftServer server;
    public ConsoleCommandSender console;
    public RemoteConsoleCommandSender remoteConsole;
    public ConsoleReader reader;

    private static MinecraftServer getServer() {


        return (Bukkit.getServer() instanceof CraftServer) ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void MinecraftServer$init(CallbackInfo ci)  {
        try {
            this.reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Inject(method = "func_240787_a_", at = @At("RETURN"))
    public void func_240787_a_(IChunkStatusListener p_240787_1_, CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));

        //this.serverConnection.acceptConnections();
    }

    public void zyyServer$setConsole(ConsoleCommandSender console) {
        this.console = console;
    }
}
