package cc.zyycc.bk.mixin.mc.inventory;

import cc.zyycc.bk.bridge.inventory.IInventoryBridge;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(net.minecraft.inventory.CraftingInventory.class)
public class CraftingInventoryMixin implements IInventoryBridge {
    @Shadow
    @Final
    private NonNullList<ItemStack> stackList;
    public List<HumanEntity> transaction = new ArrayList<>();
    @Override
    public void onOpen(CraftHumanEntity who) {
        this.transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        this.transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }
    @Override
    public List<ItemStack> getContents() {
        return this.stackList;
    }

}
