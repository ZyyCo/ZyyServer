package cc.zyycc.bk.mixin.mc.world;

import cc.zyycc.bk.bridge.WorldBridge;
import cc.zyycc.bk.bridge.server.SimpleBridge;
import cc.zyycc.bk.bridge.world.border.WorldBorderBridge;
import cc.zyycc.bk.bridge.world.storage.ServerWorldInfoBridge;
import cc.zyycc.bk.util.BKWorldFileBridge;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Mixin(net.minecraft.world.World.class)
public abstract class WorldMixin implements WorldBridge {

    private boolean bukkitConstructor = false;

    protected CraftWorld world;

    public SpigotWorldConfig spigotConfig;

    private boolean populating;

    public boolean pvpMode;

    public boolean keepSpawnInMemory = true;

    public String bkWorldName;

    public org.bukkit.generator.ChunkGenerator generator;

    protected org.bukkit.World.Environment environment;
    @Final
    @Shadow
    public ISpawnWorldInfo worldInfo;

    @Final
    @Shadow
    private DimensionType dimensionType;

    @Final
    @Shadow
    private RegistryKey<net.minecraft.world.World> dimension;

    @Shadow
    public abstract RegistryKey<net.minecraft.world.World> getDimensionKey();

    @Shadow
    public abstract IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull);

    @Final
    @Shadow
    private WorldBorder worldBorder;

    @Inject(method = "<init>(Lnet/minecraft/world/storage/ISpawnWorldInfo;Lnet/minecraft/util/RegistryKey;Lnet/minecraft/world/DimensionType;Ljava/util/function/Supplier;ZZJ)V", at = @At("RETURN"))
    private void onInitCode(ISpawnWorldInfo worldInfo, RegistryKey<net.minecraft.world.World> dimension, DimensionType dimensionType, Supplier<IProfiler> profiler, boolean isRemote, boolean isDebug, long seed, CallbackInfo ci) {
        ((WorldBorderBridge) worldBorder).bridge$setWorld((net.minecraft.world.World) (Object) this);
        if (environment == null || generator == null) {
            bkWorldName = ((IServerWorldInfo) worldInfo).getWorldName();
            environment = World.Environment.NORMAL;
            if (dimension == net.minecraft.world.World.THE_NETHER) {
                environment = World.Environment.NETHER;
                bkWorldName = "world_nether";
            } else if (dimension == net.minecraft.world.World.THE_END) {
                environment = World.Environment.THE_END;
                bkWorldName = "world_the_end";
            } else if (dimension != net.minecraft.world.World.OVERWORLD && !((ServerWorldInfoBridge) worldInfo).bridge$isBKCreated()) {
                environment = World.Environment.CUSTOM;
                bkWorldName = "world_" + dimension.getRegistryName().getPath();
            }

        }
        this.spigotConfig = new SpigotWorldConfig(bkWorldName);
        // RegistryKey<Dimension> dimensionKey,  // 维度的注册键
        // DimensionType dimensionType,         // 维度类型
        //  Supplier<Profiler> profiler,         // 性能分析器
        //  boolean isRemote,                    // 是否为远程
        //  boolean isDebug,                     // 是否是调试模式
        //  long seed,                           // 世界种子

    }

    public CraftWorld getWorld() {
        return getOrCreateCraftWorld();
    }

    @Unique
    private CraftWorld getOrCreateCraftWorld() {
        if (this.world == null) {
            return createCraftWorld();
        } else {
            return this.world;
        }
    }

    @Unique
    private CraftWorld createCraftWorld() {
        if ((net.minecraft.world.World) (Object) this instanceof net.minecraft.world.server.ServerWorld) {
            if (bkWorldName == null) {//bkNew
//                IServerWorldInfo current = (IServerWorldInfo) worldInfo;
//                while (current instanceof DerivedWorldInfo) {
//                    current = ((DerivedWorldInfo) current).delegate;
//                }
                bkWorldName = ((IServerWorldInfo) worldInfo).getWorldName();
            }

            if (generator == null) {
                generator = SimpleBridge.craftServer.getGenerator(bkWorldName);
            }

            File dimensionFolder = this.getServer().getServer().anvilConverterForAnvilFile.getDimensionFolder(dimension);
            BKWorldFileBridge.worldFilePath.put(bkWorldName, dimensionFolder);

            this.world = new CraftWorld((ServerWorld) (Object) this, generator, environment);

            ((ServerWorldInfoBridge) worldInfo).bridge$setBKWorldName(bkWorldName);
            getServer().addWorld(this.world);
        }
        return world;
    }

    @Override
    public CraftWorld bridge$getWorld() {
        return getWorld();
    }


    @Nullable
    public CraftServer getServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Override
    public CraftServer bridge$getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }


    public void bridge$setPopulating(boolean populating) {
        this.populating = populating;
    }

    @Override
    public String bridge$getWorldName() {
        return bkWorldName;
    }

    @Override
    public SpigotWorldConfig bridge$spigotConfig() {
        return this.spigotConfig;
    }


    public void bridge$setGenerator(org.bukkit.generator.ChunkGenerator generator) {
        this.generator = generator;
    }


    public void bridge$setEnvironment(org.bukkit.World.Environment environment) {
        this.environment = environment;
    }

    @Override
    public ChunkGenerator bridge$getGenerator() {
        return generator;
    }


    public Chunk getChunkAt(int x, int z){
        return (Chunk) this.getChunk(x, z, ChunkStatus.EMPTY, true);
    }
}
