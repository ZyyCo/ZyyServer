package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.EntityBridge;
import cc.zyycc.bk.bridge.server.ServerWorldBridge;
import cc.zyycc.bk.bridge.world.storage.ServerWorldInfoBridge;
import cc.zyycc.bk.mixin.mc.world.WorldMixin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R3.util.WorldUUID;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

@Mixin(net.minecraft.world.server.ServerWorld.class)
public abstract class ServerWorldMixin extends WorldMixin implements ServerWorldBridge, cc.zyycc.bk.bridge.SimpleBridge {
    public UUID uuid;
    @Final
    @Shadow
    public IServerWorldInfo serverWorldInfo;

    @Final
    @Shadow
    private Int2ObjectMap<Entity> entitiesById;

    @Final
    @Shadow
    private MinecraftServer server;

    @Unique
    private CreatureSpawnEvent.SpawnReason zyyServer$spawnReason;

    @Shadow
    public abstract boolean addEntity(Entity entityIn);

    @Shadow
    boolean tickingEntities;

    @Shadow
    protected abstract boolean hasDuplicateEntity(Entity entityIn);

    @Shadow
    protected abstract void onEntityAdded(Entity entityIn);

    @Final
    @Shadow
    private Map<UUID, Entity> entitiesByUuid;
    @Final
    @Shadow
    private ServerChunkProvider serverChunkProvider;


    @Final
    @Shadow
    private List<ServerPlayerEntity> players;




    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(MinecraftServer server, Executor backgroundExecutor, SaveFormat.LevelSave levelSave, IServerWorldInfo serverWorldInfo, RegistryKey<World> dimension, DimensionType dimensionType, IChunkStatusListener statusListener, ChunkGenerator chunkGenerator, boolean isDebug, long seed, List<ISpecialSpawner> specialSpawners, boolean shouldBeTicking, CallbackInfo ci) {

        this.pvpMode = server.isPVPEnabled();
        //        levelSave.saveDir.toFile();
        File dimensionFolder = levelSave.getDimensionFolder(this.getDimensionKey());
        this.uuid = WorldUUID.getUUID(dimensionFolder);


        if (serverWorldInfo instanceof ServerWorldInfo) {
            ServerWorldInfo info = (ServerWorldInfo) serverWorldInfo;
            if (!((ServerWorldInfoBridge) info).bridge$isBKCreated()) {
                    getWorld();
            }
        }else {
            getWorld();
        }
    }
//                if (environment != org.bukkit.World.Environment.NORMAL && environment != org.bukkit.World.Environment.CUSTOM) {
//                }

    @Override
    public IServerWorldInfo zyyServer$getWorldInfo() {
        return this.serverWorldInfo;
    }

    @Override
    public MinecraftServer zyyServer$getServer() {
        return this.server;
    }

    @Override
    public ServerWorld zyyServer$getServerWorld() {
        return (ServerWorld) (Object) this;
    }

    @Override
    public ServerWorld bridge$getMinecraftWorld() {
        return (ServerWorld) (Object) this;
    }


    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        this.zyyServer$spawnReason = reason;
        return addEntity(entity);
    }

    @Override
    public CreatureSpawnEvent.SpawnReason bridge$getSpawnReason() {
        return zyyServer$spawnReason;
    }


    @Inject(method = "addEntity0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/IChunk;"), cancellable = true)
    public void addEntity0(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        CreatureSpawnEvent.SpawnReason reason = zyyServer$spawnReason == null ? CreatureSpawnEvent.SpawnReason.DEFAULT : zyyServer$spawnReason;
        zyyServer$spawnReason = null;
        if (!CraftEventFactory.doEntityAddEventCalling((ServerWorld) (Object) this, entity, reason)) {
            cir.setReturnValue(false);
        }
    }


    @Override
    public Map<UUID, Entity> bridge$getEntitiesByUuid() {
        return entitiesByUuid;
    }

    @Override
    public Int2ObjectMap<Entity> bridge$getEntitiesById() {
        return entitiesById;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Inject(method = "updateEntity", at = @At("HEAD"), cancellable = true)
    public void updateEntity(Entity entity, CallbackInfo ci) {
//        if(entity.getClass().getName().equals("net.citizensnpcs.nms.v1_16_R3.entity.EntityHumanNPC")){
//            System.out.println("更新实体来自Class" + entity.getClass().getName());
//        }
    }


    @Inject(method = "onEntityAdded", at = @At("RETURN"))
    public void onEntityAdded(Entity entity, CallbackInfo ci) {
        ((EntityBridge) entity).bridge$isValid(true);

        //     if (entity.getClass().getName().equals("net.citizensnpcs.nms.v1_16_R3.entity.EntityHumanNPC")) {
//            EntityType horse = EntityType.HORSE;
//            CraftWorld craftWorld = getWorld();

//            System.out.println("马来了");
//            for (ServerPlayerEntity player : players) {
//
//                ((EntityBridge) entity).bridge$getBukkitEntity().teleport(new Location(craftWorld, player.getPosX(), player.getPosY(), player.getPosZ()));
//                System.out.println("npc坐标" + entity.getPosX() + " " + entity.getPosY() + " " + entity.getPosZ());
//                System.out.println("玩家坐标" + player.getPosX() + " " + player.getPosY() + " " + player.getPosZ());
//                org.bukkit.entity.Entity spawned = craftWorld.spawnEntity(new Location(craftWorld, player.getPosX(), player.getPosY(), player.getPosZ()), horse);
////                net.minecraft.entity.EntityType.ENDER_PEARL.create(this);
////                player.connection.sendPacket(new SSpawnMobPacket((LivingEntity) spawned));
//                player.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, (ServerPlayerEntity) entity));
//                player.connection.sendPacket(new SSpawnPlayerPacket((PlayerEntity) entity));
//
//            }


        //}

    }


//    @Inject(method = "addEntity0",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/IChunk;"))
//    public void addEntity1(Entity value, CallbackInfoReturnable<Boolean> cir) {
//        IChunk ichunk = this.getChunk(MathHelper.floor(value.getPosX() / (double)16.0F), MathHelper.floor(value.getPosZ() / (double)16.0F), ChunkStatus.FULL, value.forceSpawn);
//        System.out.println("来自" + ichunk);
//    }
//
//    public boolean bridge$addEntity(Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
//        AsyncCatcher.catchOp("entity add");
//        if (entity.removed)
//            return false;
//        if (hasDuplicateEntity(entity))
//            return false;
//        if (!CraftEventFactory.doEntityAddEventCalling((ServerWorld) (Object) this, entity, spawnReason))
//            return false;
//        IChunk ichunkaccess = getChunk(MathHelper.floor(entity.getPosX() / 16.0D), MathHelper.floor(entity.getPosZ() / 16.0D), ChunkStatus.FULL,
//                entity.forceSpawn);
//        if (!(ichunkaccess instanceof Chunk))
//            return false;
//        ichunkaccess.addEntity(entity);
//        onEntityAdded(entity);
//        return true;
//    }


    @Override
    public ServerWorldInfo bridge$getServerWorldInfo() {
        IServerWorldInfo current = this.serverWorldInfo;
        while (current instanceof DerivedWorldInfo) {
            current = ((DerivedWorldInfo) current).delegate;
        }
        if (current instanceof ServerWorldInfo) {
            return (ServerWorldInfo) current;
        }

        System.out.println("获取主世界ServerWorldInfo");
        return (ServerWorldInfo) Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getWorldInfo();
    }



    public Chunk getChunkIfLoaded(int x, int z) {
        return this.serverChunkProvider.getChunk(x, z, false);
    }

}
