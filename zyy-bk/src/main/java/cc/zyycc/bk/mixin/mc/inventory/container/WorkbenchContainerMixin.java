package cc.zyycc.bk.mixin.mc.inventory.container;

import cc.zyycc.bk.bridge.player.PlayerEntityBridge;
import cc.zyycc.bk.bridge.player.ServerPlayerEntityBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryView;
import org.bukkit.inventory.InventoryView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.inventory.container.WorkbenchContainer.class)
public class WorkbenchContainerMixin extends ContainerMixin {
//    private CraftInventoryPlayer inventory;

    @Shadow
    @Final
    private CraftResultInventory craftResult;
    @Shadow
    @Final
    private CraftingInventory craftMatrix;

    private CraftInventoryView bukkitEntity;

    private PlayerInventory player2;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V", at = @At("RETURN"))
    public void init(int id, PlayerInventory playerInventory, IWorldPosCallable p_i50090_3_, CallbackInfo ci) {
//        this.inventory = new CraftInventoryPlayer(playerInventory);
//        this.enderChest = new CraftInventory(entity.getInventoryEnderChest());
        this.bukkitEntity = null;
        this.player2 = playerInventory;
    }

    public InventoryView getBukkitView() {
        //ContainerWorkbench
        if (this.bukkitEntity != null) {
            return this.bukkitEntity;
        } else {
            CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftMatrix, this.craftResult);
            PlayerEntity player = this.player2.player;

            this.bukkitEntity = new CraftInventoryView(((PlayerEntityBridge) player).getBukkitEntity(), inventory,
                    (Container) (Object) this);
            return this.bukkitEntity;
        }
    }

    //检测Gui是否关闭
    @Inject(method = "canInteractWith", cancellable = true, at = @At("HEAD"))
    public void canInteractWith(PlayerEntity playerIn, CallbackInfoReturnable<Boolean> cir) {
        if(!checkReachable){//如果bukkit层没有设置checkReachable=true，则直接返回true关闭gui
            cir.setReturnValue(true);
        }
    }

}
