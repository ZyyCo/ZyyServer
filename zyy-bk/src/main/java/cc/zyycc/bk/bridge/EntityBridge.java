package cc.zyycc.bk.bridge;

import net.minecraft.command.CommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;

public interface EntityBridge {
    CraftEntity bridge$getBukkitEntity();

    CommandSender bridge$getBukkitSender(CommandSource wrapper);

    void bridge$isValid(boolean valid);
}
