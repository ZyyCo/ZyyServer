package cc.zyycc.bk.mixin.mc.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableEntity.class)
public class AgeableEntityMixin {

    public boolean ageLocked;

    @Redirect(method = "livingTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z"))
    private boolean livingTick(World world) {
        return world.isRemote || ageLocked;
    }

    @Inject(method = "writeAdditional", at = @At("RETURN"))
    private void writeAdditional(CompoundNBT compound, CallbackInfo ci) {
        compound.putBoolean("AgeLocked", ageLocked);
    }

    @Inject(method = "readAdditional", at = @At("RETURN"))
    private void readAdditional(CompoundNBT compound, CallbackInfo ci) {
        ageLocked = compound.getBoolean("AgeLocked");
    }
}
