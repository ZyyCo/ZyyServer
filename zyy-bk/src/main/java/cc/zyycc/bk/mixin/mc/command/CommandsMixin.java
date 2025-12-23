package cc.zyycc.bk.mixin.mc.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Shadow
    public abstract int handleCommand(CommandSource source, String command);
    public int a(CommandSource source, String command, String label, boolean strip) {
        return this.handleCommand(source, command);
    }

}
