package cc.zyycc.bk.mixin.mc.entity.player;

import cc.zyycc.bk.bridge.inventory.IInventoryBridge;
import cc.zyycc.bk.bridge.player.PlayerEntityBridge;
import cc.zyycc.bk.mixin.mc.inventory.IInventoryMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(net.minecraft.entity.player.PlayerInventory.class)
public abstract class PlayerInventoryMixin implements IInventoryBridge, IInventory {
    @Shadow
    @Final
    public NonNullList<ItemStack> mainInventory;
    @Shadow @Final public NonNullList<ItemStack> offHandInventory;
    @Shadow @Final public NonNullList<ItemStack> armorInventory;
    @Shadow @Final private List<NonNullList<ItemStack>> allInventories;
    @Shadow @Final public PlayerEntity player;

    public List<HumanEntity> transaction = new ArrayList<>();
    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }
    public List<HumanEntity> getViewers() {
        return transaction;
    }
    public List<ItemStack> getContents() {
        List<ItemStack> combined = new ArrayList<>(mainInventory.size() + offHandInventory.size() + armorInventory.size());
        for (List<ItemStack> sub : this.allInventories) {
            combined.addAll(sub);
        }
        return combined;
    }
    //@Override
    public InventoryHolder getOwner() {
        return ((PlayerEntityBridge) this.player).getBukkitEntity();
    }
}
