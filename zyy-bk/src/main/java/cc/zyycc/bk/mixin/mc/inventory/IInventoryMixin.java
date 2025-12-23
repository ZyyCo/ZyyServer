package cc.zyycc.bk.mixin.mc.inventory;

import cc.zyycc.bk.bridge.inventory.IInventoryBridge;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(net.minecraft.inventory.IInventory.class)
public interface IInventoryMixin extends IInventoryBridge {

    default void onOpen(CraftHumanEntity who) {
    }

    default void onClose(CraftHumanEntity who) {
    }


    default List<HumanEntity> getViewers() {
        return new ArrayList<>();
    }

    default List<ItemStack> getContents() {
        return new ArrayList<>();
    }

}
