package cc.zyycc.bk.mixin.mc.inventory.container.inventory;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.inventory.IInventory.class)
public interface IInventoryMixin {

    default void onOpen(CraftHumanEntity who) {
    }
    default void onClose(CraftHumanEntity who) {
    }

}
