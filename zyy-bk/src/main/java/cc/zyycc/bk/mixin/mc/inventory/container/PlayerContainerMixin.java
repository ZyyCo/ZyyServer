package cc.zyycc.bk.mixin.mc.inventory.container;

import cc.zyycc.bk.bridge.player.PlayerEntityBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.text.TranslationTextComponent;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerContainer.class)
public abstract class PlayerContainerMixin extends ContainerMixin {
    @Shadow
    @Final
    private CraftResultInventory craftResult;
    private CraftInventoryView bukkitEntity;
    @Shadow
    @Final
    private CraftingInventory craftMatrix;
    private PlayerInventory player2;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn, CallbackInfo ci) {
        this.player2 = playerInventory;
        this.setTitle(new TranslationTextComponent("container.crafting"));
    }

    public InventoryView getBukkitView() {
        if (this.bukkitEntity != null){
            return this.bukkitEntity;
        }
        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftMatrix, this.craftResult);

        PlayerEntity player1 = player2.player;
        this.bukkitEntity = new CraftInventoryView((HumanEntity)((PlayerEntityBridge) player1).getBukkitEntity(), (Inventory)inventory, (PlayerContainer)(Object)this);
        return this.bukkitEntity;

    }
}
