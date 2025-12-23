package cc.zyycc.bk.bridge.command;

import cc.zyycc.bk.bridge.EntityBridge;
import cc.zyycc.bk.bridge.network.RConConsoleSourceBridge;
import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import cc.zyycc.bk.bridge.tileentity.CommandBlockLogicBridge;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import org.bukkit.command.CommandSender;

public interface ICommandSourceBridge {

    default CommandSender bridge$getBukkitSender(CommandSource commandSource) {

        if (this instanceof Entity) {
            EntityBridge entity = (EntityBridge) this;
            return entity.bridge$getBukkitSender(commandSource);
        }

        if (this instanceof MinecraftServer) {
            MinecraftServerBridge server = (MinecraftServerBridge) this;
            return server.bridge$getBukkitSender(commandSource);
        }

        if (this instanceof CommandBlockLogic) {
            return org.bukkit.Bukkit.getConsoleSender();
        }
        if (this instanceof RConConsoleSource) {
            RConConsoleSourceBridge rConConsoleSource = (RConConsoleSourceBridge) this;
            return rConConsoleSource.bridge$getBukkitSender(commandSource);
        }

        return org.bukkit.Bukkit.getConsoleSender();
    }
}
