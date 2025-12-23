package cc.zyycc.bk.mixin.mc.inventory;

import cc.zyycc.bk.mixin.mc.inventory.container.ContainerMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.inventory.container.AbstractRepairContainer.class)
public abstract class AbstractRepairContainerMixin extends ContainerMixin {
    @Shadow
    @Final
    protected IWorldPosCallable field_234644_e_;
    @Shadow
    @Final
    protected PlayerEntity field_234645_f_;
    @Shadow
    @Final
    protected IInventory field_234643_d_;
    @Shadow
    @Final
    protected CraftResultInventory field_234642_c_;

    @Inject(method = "canInteractWith", cancellable = true, at = @At("HEAD"))
    private void canInteractWith(PlayerEntity playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!checkReachable) {
            cir.setReturnValue(true);
        }
    }

}
