package cc.zyycc.bk.mixin.mc.player;

import cc.zyycc.bk.bridge.player.ServerPlayerEntityBridge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.SpawnLocationHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.FoodStats;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin implements ServerPlayerEntityBridge {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    protected abstract int func_205735_q(int p_205735_1_);


    @Shadow private RegistryKey<World> field_241137_cq_;

    public boolean keepLevel = false;
    public boolean relativeTime = true;
    @Shadow public abstract void giveExperiencePoints(int p_195068_1_);

    @Shadow
    public abstract void getNextWindowId();

    @Shadow
    public int currentWindowId;
    @Shadow
    public int lastExperience;
    public ITextComponent listName;
    public int newExp = 0;
    public double maxHealthCache;
    public String displayName;
    public Location compassTarget;
    public boolean joining = true;
    public int newLevel;
    public int newTotalExp = 0;
    public long timeOffset = 0;
    public WeatherType weather = null;
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        this.displayName = this.getGameProfile().getName();
        this.maxHealthCache = this.getMaxHealth();
    }


    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }

    public int nextContainerCounter() {
        this.getNextWindowId();
        return this.currentWindowId;
    }

    @Override
    public void bridge$setCompassTarget(Location compassTarget) {
        this.compassTarget = compassTarget;
    }

    @Override
    public BlockPos bridge$getSpawnPoint(ServerWorld world) {
        return this.getSpawnPoint(world);
    }

    public final BlockPos getSpawnPoint(ServerWorld serverWorld) {
        BlockPos blockPos = serverWorld.getSpawnPoint();
        if (serverWorld.getDimensionType().hasSkyLight() && serverWorld.serverWorldInfo.getGameType() != GameType.ADVENTURE) {
            int i = Math.max(0, this.server.getSpawnRadius(serverWorld));
            int j = MathHelper.floor(serverWorld.getWorldBorder().getClosestDistance((double) blockPos.getX(), (double) blockPos.getZ()));
            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            long k = (long) (i * 2 + 1);
            long l = k * k;
            int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int) l;
            int j1 = this.func_205735_q(i1);
            int k1 = (new Random()).nextInt(i1);

            for (int l1 = 0; l1 < i1; ++l1) {
                int i2 = (k1 + j1 * l1) % i1;
                int j2 = i2 % (i * 2 + 1);
                int k2 = i2 / (i * 2 + 1);
                BlockPos blockposition1 = SpawnLocationHelper
                        .func_241092_a_(serverWorld, blockPos.getX() + j2 - i, blockPos.getZ() + k2 - i, false);
                if (blockposition1 != null) {
                    return blockposition1;
                }
            }
        }

        return blockPos;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.joining) {
            this.joining = false;
        }
    }

    @Override
    public boolean bridge$isJoining() {
        return joining;
    }

    @Override
    public void bridge$reset() {
        reset();
    }

    public void reset() {
        float exp = 0.0F;
        boolean keepInventory = this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
        if (this.keepLevel || keepInventory) {
            exp = this.experience;
            this.newTotalExp = this.experienceTotal;
            this.newLevel = this.experienceLevel;
        }

        this.setHealth(this.getMaxHealth());
        this.fire = 0;
        this.fallDistance = 0.0F;
        this.foodStats = new FoodStats();
        this.experienceLevel = this.newLevel;
        this.experienceTotal = this.newTotalExp;
        this.experience = 0.0F;
        this.deathTime = 0;
        this.setArrowCount(0, true);
        this.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH);
        this.potionsNeedUpdate = true;
        this.openContainer = this.container;
        this.attackingPlayer = null;
        this.revengeTarget = null;
        this.combatTracker = new CombatTracker((ServerPlayerEntity) (Object) this);
        this.lastExperience = -1;
        if (!this.keepLevel && !keepInventory) {
            this.experience = exp;
        } else {
            this.giveExperiencePoints(this.newExp);
        }

        this.keepLevel = false;
    }

    @Inject(method = "playerTick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;ticksExisted:I"))
    private void playerTick(CallbackInfo ci) {
        if (this.maxHealthCache != this.getMaxHealth()) {
            this.getBukkitEntity().updateScaledHealth();
        }
    }



    @Inject(method = "readAdditional", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;isSleeping()Z",shift = At.Shift.AFTER))
    private void readAdditional(CompoundNBT compound, CallbackInfo ci) {
        this.getBukkitEntity().readExtraData(compound);//小心主城传送

        String spawnWorld = compound.getString("SpawnWorld");
        CraftWorld oldWorld = (CraftWorld)Bukkit.getWorld(spawnWorld);
        if (oldWorld != null) {
            this.field_241137_cq_ = oldWorld.getHandle().getDimensionKey();
        }

    }


    @Inject(method = "writeAdditional", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnePlayerRiding()Z"))
    private void writeAdditional(CompoundNBT compoundNBT, CallbackInfo ci) {
        if (!this.persist) {
            compoundNBT.putBoolean("Bukkit.persist", this.persist);
        }
//        if (this.persistentInvisibility) {
//            compoundNBT.putBoolean("Bukkit.invisible", this.persistentInvisibility);
//        }


    }
    @Inject(method = "writeAdditional", at = @At("RETURN"))
    private void writeAdditional2(CompoundNBT compound, CallbackInfo ci) {
        if (this.getBukkitEntity() != null) {
            this.getBukkitEntity().storeBukkitValues(compound);
            this.getBukkitEntity().setExtraData(compound);
        }
    }

    public long getPlayerTime() {
        if (this.relativeTime) {
            return this.world.getDayTime() + this.timeOffset;
        }
        return this.world.getDayTime() - this.world.getDayTime() % 24000L + this.timeOffset;
    }

    public WeatherType getPlayerWeather() {
        return this.weather;
    }







}
