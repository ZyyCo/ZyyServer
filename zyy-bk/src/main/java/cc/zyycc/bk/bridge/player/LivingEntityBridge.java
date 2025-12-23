package cc.zyycc.bk.bridge.player;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;

public interface LivingEntityBridge {
    void bridge$setDead(boolean dead);

    CraftLivingEntity bridge$getBukkitEntity();
}
