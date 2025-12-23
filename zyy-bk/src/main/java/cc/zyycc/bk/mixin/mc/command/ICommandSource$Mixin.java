package cc.zyycc.bk.mixin.mc.command;

import cc.zyycc.bk.bridge.command.ICommandSourceBridge;
import net.minecraft.command.CommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/command/ICommandSource$1")
public class ICommandSourceMixin1 implements ICommandSourceBridge {

    @Override
    public CommandSender bridge$getBukkitSender(CommandSource commandSource) {
        return getBukkitSender(wrapper);
    }
}
