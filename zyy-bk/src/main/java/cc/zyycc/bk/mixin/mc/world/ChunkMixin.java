package cc.zyycc.bk.mixin.mc.world;

import cc.zyycc.bk.bridge.WorldBridge;
import cc.zyycc.bk.bridge.world.ChunkBridge;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Consumer;

@Mixin(net.minecraft.world.chunk.Chunk.class)
public abstract class ChunkMixin implements ChunkBridge {
    @Shadow
    @Final
    private ChunkPos pos;
    @Shadow
    @Final
    private World world;
    @Shadow
    private volatile boolean dirty;
    @Shadow
    private boolean hasEntities;

    @Shadow
    private long lastSaveTime;

    public ServerWorld serverWorld;

    @Shadow
    public abstract boolean isModified();

    public org.bukkit.Chunk bukkitChunk;
    public boolean mustNotSave;

    //11111
    public boolean needsDecoration;

    public void bridge$loadCallback() {
        loadCallback();
    }

    public void bridge$unloadCallback() {
        unloadCallback();
    }


    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/BiomeContainer;Lnet/minecraft/util/palette/UpgradeData;Lnet/minecraft/world/ITickList;Lnet/minecraft/world/ITickList;J[Lnet/minecraft/world/chunk/ChunkSection;Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void init2(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn, UpgradeData upgradeDataIn, ITickList<Block> tickBlocksIn, ITickList<Fluid> tickFluidsIn, long inhabitedTimeIn, ChunkSection[] sectionsIn, Consumer<Chunk> postLoadConsumerIn, CallbackInfo ci) {
        this.serverWorld = (ServerWorld) world;
        this.bukkitChunk = new CraftChunk((Chunk) (Object) this);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ChunkPrimer;)V", at = @At("RETURN"))
    public void init(World worldIn, ChunkPrimer primer, CallbackInfo ci) {
        this.needsDecoration = true;
    }

    public void unloadCallback() {
        org.bukkit.Server server = Bukkit.getServer();
        org.bukkit.event.world.ChunkUnloadEvent unloadEvent = new org.bukkit.event.world.ChunkUnloadEvent(this.bukkitChunk, this.isModified());
        server.getPluginManager().callEvent(unloadEvent);
        this.mustNotSave = !unloadEvent.isSaveChunk();
    }


    public void loadCallback() {
        Server server = ((WorldBridge) world).bridge$getCraftServer();
        if (server != null) {
            ChunkLoadEvent chunkLoadEvent = new ChunkLoadEvent(this.bukkitChunk, this.needsDecoration);
//            if (chunkLoadEvent.getChunk().getX() == -10 && chunkLoadEvent.getChunk().getZ() == -114) {
//                System.out.println("区块坐标被加载了  " + chunkLoadEvent.getChunk().getX() + "," +
//                        chunkLoadEvent.getChunk().getZ());
//                System.out.println("UUID:" + Bukkit.getWorld("world").getUID());
//                System.out.println("BUKKIT世界:" + Bukkit.getWorld("world"));
//                System.out.println("世界:" + ((ServerWorldBridge) serverWorld).uuid());
//            }
            server.getPluginManager().callEvent(chunkLoadEvent);
            if (this.needsDecoration) {
                this.needsDecoration = false;
                Random random = new Random();
                random.setSeed(((ServerWorld) world).getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) this.pos.x * xRand + (long) this.pos.z * zRand ^ ((ServerWorld) world).getSeed());
                org.bukkit.World world = ((WorldBridge) this.world).bridge$getWorld();
                if (world != null) {
                    ((WorldBridge) this.world).bridge$setPopulating(true);

                    try {
                        for (BlockPopulator populator : world.getPopulators()) {
                            populator.populate(world, random, this.bukkitChunk);
                        }
                    } finally {
                        ((WorldBridge) this.world).bridge$setPopulating(false);
                    }
                }
                server.getPluginManager().callEvent(new ChunkPopulateEvent(this.bukkitChunk));
            }
        }

    }


    @Inject(method = "isModified", at = @At("HEAD"), cancellable = true)
    public void isModified(CallbackInfoReturnable<Boolean> cir) {
        if (this.mustNotSave) {
            cir.setReturnValue(false);
        }
    }

    public org.bukkit.Chunk getBukkitChunk() {
        return bukkitChunk;
    }
}
