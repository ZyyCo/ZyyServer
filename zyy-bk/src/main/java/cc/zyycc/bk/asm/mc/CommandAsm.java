package cc.zyycc.bk.asm.mc;

import cc.zyycc.bk.asm.util.HookResult;
import cc.zyycc.bk.bridge.player.ServerPlayerEntityBridge;
import cc.zyycc.bk.bridge.server.SimpleBridge;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.v1_16_R3.SpigotTimings;
import org.bukkit.craftbukkit.v1_16_R3.util.LazyPlayerSet;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.spigotmc.SpigotConfig;

public class CommandAsm {
    public static CommandDispatcher<CommandSource> getConstructor(Commands commands, CommandDispatcher<CommandSource> dispatcher) {
        if (dispatcher == null) {
            dispatcher = new CommandDispatcher<>();
        }

        dispatcher.setConsumer((p_197058_0_, success, result) ->
        {
            p_197058_0_.getSource().onCommandComplete(p_197058_0_, success, result);
        });
        return dispatcher;
    }


    public static HookResult handleCommandAsm(Commands commands, CommandSource source, String command) {
        if (source.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
            SpigotTimings.playerCommandTimer.startTiming();
            DedicatedServer minecraftServer = SimpleBridge.craftServer.getServer();
            if (SpigotConfig.logCommands) {
               // LOGGER.info(player.getName() + " issued server command: " + command);
                PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(((ServerPlayerEntityBridge) player).getBukkitEntity(), command, new LazyPlayerSet(minecraftServer));
                SimpleBridge.craftServer.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    SpigotTimings.playerCommandTimer.stopTiming();
                    return HookResult.cancelWith(1);
                } else {
                    return null;
//                    try {
//                        if (!CraftServerBridge.craftServer.dispatchCommand(event.getPlayer(), event.getMessage().substring(1))) {
//                            return;
//                        }
//
//                        return;
//                    } catch (CommandException var8) {
//                        player.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
//                        java.util.logging.Logger.getLogger(PlayerConnection.class.getName()).log(Level.SEVERE, (String) null, var8);
//                    } finally {
//                        SpigotTimings.playerCommandTimer.stopTiming();
//                    }
                }


            }
        }
        return null;
    }
}
