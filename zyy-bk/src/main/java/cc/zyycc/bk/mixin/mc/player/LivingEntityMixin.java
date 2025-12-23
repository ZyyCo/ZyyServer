package cc.zyycc.bk.mixin.mc.player;

import cc.zyycc.bk.bridge.player.LivingEntityBridge;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_16_R3.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.entity.ArrowBodyCountChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin implements LivingEntityBridge {
    @Shadow
    protected boolean dead;

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public int deathTime;
    @Shadow
    public boolean potionsNeedUpdate;
    @Shadow
    protected PlayerEntity attackingPlayer;
    @Shadow
    @Nullable
    public LivingEntity revengeTarget;
    @Shadow
    public CombatTracker combatTracker;
    @Shadow
    @Final
    private static DataParameter<Integer> ARROW_COUNT_IN_ENTITY;

    @Shadow
    public abstract int getArrowCountInEntity();

    @Shadow
    public abstract boolean clearActivePotions();

    @Shadow
    @Final
    private AttributeModifierManager attributes;
    @Final
    @Shadow
    public Map<Effect, EffectInstance> activePotionsMap;

    @Shadow
    protected abstract void onFinishedPotionEffect(EffectInstance effect);

    @Shadow
    public abstract void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack);

    @Shadow protected abstract void playEquipSound(ItemStack stack);

    public boolean canPickUpLoot;

    public CraftAttributeMap craftAttributes;

    public int maxAirTicks;

    public int expToDrop;


    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(EntityType<? extends LivingEntity> type, World p_i48577_2_, CallbackInfo ci) {
        this.maxAirTicks = 300;
        this.craftAttributes = new CraftAttributeMap(this.attributes);
    }


    public final void setArrowCount(int i, boolean flag) {

        ArrowBodyCountChangeEvent event = CraftEventFactory.callArrowBodyCountChangeEvent((LivingEntity) (Object) this, this.getArrowCountInEntity(), i, flag);
        if (!event.isCancelled()) {
            this.dataManager.set(ARROW_COUNT_IN_ENTITY, event.getNewAmount());
        }
    }

    public boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        if (this.world.isRemote) {
            return false;
        } else {
            Iterator<EffectInstance> iterator = this.activePotionsMap.values().iterator();

            boolean flag;
            for (flag = false; iterator.hasNext(); flag = true) {
                EffectInstance effect = (EffectInstance) iterator.next();
                EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent((LivingEntity) (Object) this, effect, (EffectInstance) null, cause, EntityPotionEffectEvent.Action.CLEARED);
                if (!event.isCancelled()) {
                    this.onFinishedPotionEffect(effect);
                    iterator.remove();
                }
            }

            return flag;
        }
    }

    public void bridge$setDead(boolean dead) {
        this.dead = dead;
    }


    @Override
    public CraftLivingEntity bridge$getBukkitEntity() {
        return (CraftLivingEntity) super.getBukkitEntity();
    }


    public void setSlot(EquipmentSlotType slotIn, ItemStack stack, boolean silent) {
        this.setItemStackToSlot(slotIn, stack);
    }

    protected void playEquipSound(ItemStack stack, boolean silent) {
        if (!stack.isEmpty()  && !silent) {
            SoundEvent soundevent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = stack.getItem();
            if (item instanceof ArmorItem) {
                soundevent = ((ArmorItem)item).getArmorMaterial().getSoundEvent();
            } else if (item == Items.ELYTRA) {
                soundevent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
        }
    }

//    protected void dropExperience() {
//        int i = this.expToDrop;
//
//        while(i > 0) {
//            int j = EntityExperienceOrb.getOrbValue(i);
//            i -= j;
//            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX(), this.locY(), this.locZ(), j));
//        }
//
//        this.expToDrop = 0;
//    }
//


}
