package cc.zyycc.bk.bridge.inventory;

import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;


public interface IInventoryBridge {

    default void onOpen(CraftHumanEntity who) {
    }
    default void onClose(CraftHumanEntity who) {
    }

    default List<HumanEntity> getViewers() {
        return null;
    }

    default List<ItemStack> getContents() {
        return new ArrayList<>();
    }

}
