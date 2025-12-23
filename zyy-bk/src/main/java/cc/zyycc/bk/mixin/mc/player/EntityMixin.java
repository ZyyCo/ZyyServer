package cc.zyycc.bk.mixin.mc.player;

import net.minecraft.entity.Entity;
import net.minecraft.item.TallBlockItem;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public float rotationYaw;
    @Shadow
    public World world;
    private CraftEntity bukkitEntity;

    @Shadow
    public abstract double getPosX();

    @Shadow
    public abstract double getPosZ();

    @Shadow
    public abstract double getPosY();

    @Shadow
    public abstract double getPosYEye();

    @Shadow
    public int fire;
    @Shadow
    public float fallDistance;

    @Shadow
    @Final
    protected EntityDataManager dataManager;

    @Shadow
    public abstract Entity getRidingEntity();

    @Shadow
    public abstract void playSound(SoundEvent soundIn, float volume, float pitch);

    public boolean persist = true;

    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftEntity.getEntity((CraftServer) Bukkit.getServer(), (Entity) (Object) this);
        }
        return this.bukkitEntity;
    }

    public float getBukkitYaw() {
        return rotationYaw;
    }

    public boolean isChunkLoaded() {
        return this.world.chunkExists((int) Math.floor(getPosX()) >> 4, (int) Math.floor(getPosZ()) >> 4);
    }
}
