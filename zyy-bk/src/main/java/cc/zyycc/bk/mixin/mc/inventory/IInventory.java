package cc.zyycc.bk.mixin.mc.inventory;

import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.inventory.IInventory.class)
public interface IInventory {

    default InventoryHolder getOwner() {
        System.out.println("知道了getOwner这里是null");
        return null;
    }
}
