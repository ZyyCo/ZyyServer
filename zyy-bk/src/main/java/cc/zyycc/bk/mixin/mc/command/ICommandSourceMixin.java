package cc.zyycc.bk.mixin.mc.command;

import cc.zyycc.bk.bridge.command.ICommandSourceBridge;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ICommandSource.class)
public interface ICommandSourceMixin extends ICommandSourceBridge {
    default CommandSender getBukkitSender(CommandSource commandSource) {
        return this.bridge$getBukkitSender(commandSource);
    }
}
