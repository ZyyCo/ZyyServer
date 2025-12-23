package cc.zyycc.bk.mixin.mc.player;

import cc.zyycc.bk.bridge.player.PlayerEntityBridge;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements PlayerEntityBridge {

    public boolean fauxSleeping;

    public BlockPos spawnPos;
    @Shadow public float experience;

    @Shadow
    public abstract GameProfile getGameProfile();

    @Shadow public int experienceTotal;
    @Shadow public int experienceLevel;
    @Shadow protected FoodStats foodStats;
    @Shadow @Final public PlayerContainer container;
    @Shadow public Container openContainer;
    @Shadow @Final public PlayerInventory inventory;



    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(World p_i241920_1_, BlockPos spawnPos, float p_i241920_3_, GameProfile p_i241920_4_, CallbackInfo ci) {
        this.spawnPos = spawnPos;
    }



    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }





    public void setSlot(EquipmentSlotType enumitemslot, ItemStack itemstack, boolean silent) {
        if (enumitemslot == EquipmentSlotType.MAINHAND) {
            this.playEquipSound(itemstack, silent);
            this.inventory.mainInventory.set(this.inventory.currentItem, itemstack);
        } else if (enumitemslot == EquipmentSlotType.OFFHAND) {
            this.playEquipSound(itemstack, silent);
            this.inventory.offHandInventory.set(0, itemstack);
        } else if (enumitemslot.getSlotType() ==  EquipmentSlotType.Group.ARMOR) {
            this.playEquipSound(itemstack, silent);
            this.inventory.armorInventory.set(enumitemslot.getIndex(), itemstack);
        }
    }


    public GameProfile getProfile(){
        return this.getGameProfile();
    }

}
