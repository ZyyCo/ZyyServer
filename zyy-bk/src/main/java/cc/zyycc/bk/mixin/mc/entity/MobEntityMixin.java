package cc.zyycc.bk.mixin.mc.entity;

import cc.zyycc.bk.bridge.SimpleBridge;
import cc.zyycc.bk.bridge.player.LivingEntityBridge;

import cc.zyycc.bk.mixin.mc.player.LivingEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntityMixin {
    @Shadow private LivingEntity attackTarget;
    @Shadow
    public abstract LivingEntity getAttackTarget();
    public void setGoalTarget(@Nullable LivingEntity entityliving) {
        this.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }
    public boolean setGoalTarget(LivingEntity livingEntity, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (this.getAttackTarget() == livingEntity) {
            return false;
        } else {
            if (fireEvent) {
                if (reason == EntityTargetEvent.TargetReason.UNKNOWN && this.getAttackTarget() != null && livingEntity == null) {
                    reason = this.getAttackTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
                }

                if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                   // this.world.getServer().getLogger().log(Level.WARNING, "Unknown target reason, please report on the issue tracker", new Exception());
                }

                CraftLivingEntity ctarget = null;
                if (livingEntity != null) {
                    ctarget = ((LivingEntityBridge) livingEntity).bridge$getBukkitEntity();
                }

                EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);

                ((SimpleBridge)this.world.getServer()).bridge$getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }

                if (event.getTarget() != null) {
                    livingEntity = ((CraftLivingEntity)event.getTarget()).getHandle();
                } else {
                    livingEntity = null;
                }
            }
            this.attackTarget = livingEntity;
            return true;
        }
    }

}
