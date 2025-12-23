package cc.zyycc.bk.bridge.server;

import net.minecraft.command.CommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

public interface MinecraftServerBridge {

    void bridge$setConsole(ConsoleCommandSender console);

    RemoteConsoleCommandSender bridge$getRemoteConsole();

    CommandSender bridge$getBukkitSender(CommandSource commandSource);

    boolean bridge$hasStopped();

    public void bridge$queuedProcess(Runnable runnable);
}
