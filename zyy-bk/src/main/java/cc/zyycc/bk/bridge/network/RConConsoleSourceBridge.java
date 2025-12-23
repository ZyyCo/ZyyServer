package cc.zyycc.bk.bridge.network;

import net.minecraft.command.CommandSource;
import org.bukkit.command.CommandSender;

public interface RConConsoleSourceBridge {
    CommandSender bridge$getBukkitSender(CommandSource commandSource);
}
