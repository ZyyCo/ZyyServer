package cc.zyycc.bk.mixin.mc.network;

import cc.zyycc.bk.bridge.player.ServerPlayerEntityBridge;
import cc.zyycc.bk.bridge.server.CraftServerBridge;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.server.*;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mixin(ServerPlayNetHandler.class)
public abstract class ServerPlayNetHandlerMixin {
    @Shadow
    private double lowestRiddenX;
    @Shadow
    private double lowestRiddenY;
    @Shadow
    private double lowestRiddenZ;
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    private Vector3d targetPos;

    @Shadow
    private int teleportId;


    @Shadow
    public abstract void setPlayerLocation(double p_175089_1_, double p_175089_3_, double p_175089_5_, float p_175089_7_, float p_175089_8_, Set<SPlayerPositionLookPacket.Flags> p_175089_9_);


    @Shadow
    public abstract void disconnect(ITextComponent p_194028_1_);

    @Shadow
    private int lastPositionUpdate;
    @Shadow
    private int networkTickCount;
    public boolean processedDisconnect;
    private boolean justTeleported;
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private float lastYaw;
    private float lastPitch;

    public CraftPlayer getPlayer() {
        return this.player == null ? null : ((ServerPlayerEntityBridge) this.player).getBukkitEntity();
    }

    public void teleport(Location location) {
        this.internalTeleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), Collections.emptySet());
    }

    private void internalTeleport(double d0, double d1, double d2, float f, float f1, Set<SPlayerPositionLookPacket.Flags> set) {
        if (Float.isNaN(f)) {
            f = 0.0F;
        }

        if (Float.isNaN(f1)) {
            f1 = 0.0F;
        }

        this.justTeleported = true;
        double d3 = set.contains(SPlayerPositionLookPacket.Flags.X) ? this.player.getPosX() : (double) 0.0F;
        double d4 = set.contains(SPlayerPositionLookPacket.Flags.Y) ? this.player.getPosY() : (double) 0.0F;
        double d5 = set.contains(SPlayerPositionLookPacket.Flags.Z) ? this.player.getPosZ() : (double) 0.0F;
        float f2 = set.contains(SPlayerPositionLookPacket.Flags.Y_ROT) ? this.player.rotationYaw : 0.0F;
        float f3 = set.contains(SPlayerPositionLookPacket.Flags.X_ROT) ? this.player.rotationPitch : 0.0F;
        this.targetPos = new Vector3d(d0, d1, d2);
        if (++this.teleportId == Integer.MAX_VALUE) {
            this.teleportId = 0;
        }

        this.lastPosX = this.targetPos.x;
        this.lastPosY = this.targetPos.y;
        this.lastPosZ = this.targetPos.z;
        this.lastYaw = f;
        this.lastPitch = f1;
        this.lastPositionUpdate = this.networkTickCount;
        this.player.setPositionAndRotation(d0, d1, d2, f, f1);
        this.player.connection.sendPacket(new SPlayerPositionLookPacket(d0 - d3, d1 - d4, d2 - d5, f - f2, f1 - f3, set, this.teleportId));
    }


    public boolean setPlayerLocation(double d0, double d1, double d2, float f, float f1, Set<SPlayerPositionLookPacket.Flags> set, PlayerTeleportEvent.TeleportCause cause) {
        System.out.println("知道了 setPlayerLocation" + this);
        Player player = this.getPlayer();
        Location from = player.getLocation();
        Location to = new Location(this.getPlayer().getWorld(), d0, d1, d2, f, f1);
        if (from.equals(to)) {
            this.internalTeleport(d0, d1, d2, f, f1, set);
            return false;
        } else {
            PlayerTeleportEvent event = new PlayerTeleportEvent(player, from.clone(), to.clone(), cause);
            CraftServerBridge.craftServer.getPluginManager().callEvent(event);
            if (event.isCancelled() || !to.equals(event.getTo())) {
                set.clear();
                to = event.isCancelled() ? event.getFrom() : event.getTo();
                d0 = to.getX();
                d1 = to.getY();
                d2 = to.getZ();
                f = to.getYaw();
                f1 = to.getPitch();
            }

            this.internalTeleport(d0, d1, d2, f, f1, set);
            return event.isCancelled();
        }
    }

    @Unique
    private static boolean zyyServer$isMoveVehiclePacketInvalid(CMoveVehiclePacket p_184341_0_) {
        return !Doubles.isFinite(p_184341_0_.getX()) || !Doubles.isFinite(p_184341_0_.getY()) || !Doubles.isFinite(p_184341_0_.getZ()) || !Floats.isFinite(p_184341_0_.getPitch()) || !Floats.isFinite(p_184341_0_.getYaw());
    }

    @Inject(method = "processVehicleMove", at = @At("HEAD"))
    public void processVehicleMove(CMoveVehiclePacket p_184338_1_, CallbackInfo ci) {
        System.out.println("知道了 processVehicleMove" + this);
//        PacketThreadUtil.checkThreadAndEnqueue(p_184338_1_, (ServerPlayNetHandler) (Object) this, this.player.getServerWorld());
//        if (zyyServer$isMoveVehiclePacketInvalid(p_184338_1_)) {
//            this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
//        } else {
//            Entity entity = this.player.getLowestRidingEntity();
//            if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lowestRiddenEnt) {
//                ServerWorld serverworld = this.player.getServerWorld();
//                double d0 = entity.getPosX();
//                double d1 = entity.getPosY();
//                double d2 = entity.getPosZ();
//                double d3 = p_184338_1_.getX();
//                double d4 = p_184338_1_.getY();
//                double d5 = p_184338_1_.getZ();
//                float f = p_184338_1_.getYaw();
//                float f1 = p_184338_1_.getPitch();
//                double d6 = d3 - this.lowestRiddenX;
//                double d7 = d4 - this.lowestRiddenY;
//                double d8 = d5 - this.lowestRiddenZ;
//                double d9 = entity.getMotion().lengthSquared();
//                double d10 = d6 * d6 + d7 * d7 + d8 * d8;
//                if (d10 - d9 > (double) 100.0F && !this.func_217264_d()) {
//                    //   LOGGER.debug("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
//                    this.netManager.sendPacket(new SMoveVehiclePacket(entity));
//                    return;
//                }
//
//                boolean flag = serverworld.hasNoCollisions(entity, entity.getBoundingBox().shrink((double) 0.0625F));
//                d6 = d3 - this.lowestRiddenX1;
//                d7 = d4 - this.lowestRiddenY1 - 1.0E-6;
//                d8 = d5 - this.lowestRiddenZ1;
//                entity.move(MoverType.PLAYER, new Vector3d(d6, d7, d8));
//                d6 = d3 - entity.getPosX();
//                d7 = d4 - entity.getPosY();
//                if (d7 > (double) -0.5F || d7 < (double) 0.5F) {
//                    d7 = (double) 0.0F;
//                }
//
//                d8 = d5 - entity.getPosZ();
//                d10 = d6 * d6 + d7 * d7 + d8 * d8;
//                boolean flag1 = false;
//                if (d10 > (double) 0.0625F) {
//                    flag1 = true;
//                    LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), this.player.getName().getString(), Math.sqrt(d10));
//                }
//
//                entity.setPositionAndRotation(d3, d4, d5, f, f1);
//                this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch);
//                boolean flag2 = serverworld.hasNoCollisions(entity, entity.getBoundingBox().shrink((double) 0.0625F));
//                if (flag && (flag1 || !flag2)) {
//                    entity.setPositionAndRotation(d0, d1, d2, f, f1);
//                    this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch);
//                    this.netManager.sendPacket(new SMoveVehiclePacket(entity));
//                    return;
//                }
//
//                this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
//                this.player.addMovementStat(this.player.getPosX() - d0, this.player.getPosY() - d1, this.player.getPosZ() - d2);
//                this.vehicleFloating = d7 >= (double) -0.03125F && !this.server.isFlightAllowed() && this.func_241162_a_(entity);
//                this.lowestRiddenX1 = entity.getPosX();
//                this.lowestRiddenY1 = entity.getPosY();
//                this.lowestRiddenZ1 = entity.getPosZ();
//            }
        //}

    }

    @Inject(method = "setPlayerLocation(DDDFFLjava/util/Set;)V", at = @At("HEAD"))
    public void setPlayerLocation(double p_175089_1_, double p_175089_3_, double p_175089_5_, float p_175089_7_, float p_175089_8_, Set<SPlayerPositionLookPacket.Flags> p_175089_9_, CallbackInfo ci) {
        System.out.println("知道了setPlayerLocation");
    }


    @Inject(method = "sendPacket(Lnet/minecraft/network/IPacket;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/IPacket;Lio/netty/util/concurrent/GenericFutureListener;)V"))
    private void sendPacket(IPacket<?> packetIn, GenericFutureListener<? extends Future<? super Void>> futureListeners, CallbackInfo ci) {

//        if (packetIn instanceof STeamsPacket) {
//            System.out.println("packetIn" + packetIn);
//            System.out.println("futureListeners" + futureListeners);
//        }
        if (packetIn != null && !this.processedDisconnect) {
            if (packetIn instanceof SWorldSpawnChangedPacket) {
                SWorldSpawnChangedPacket packet6 = (SWorldSpawnChangedPacket) packetIn;
                ((ServerPlayerEntityBridge) this.player).bridge$setCompassTarget(new Location(this.getPlayer().getWorld(), (double) packet6.spawnPos.getX(), (double) packet6.spawnPos.getY(), (double) packet6.spawnPos.getZ()));
            }
        }
    }
}
