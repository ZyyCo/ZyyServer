package cc.zyycc.bk.mixin.mc.server;

import cc.zyycc.bk.bridge.SimpleBridge;
import cc.zyycc.bk.bridge.WorldBridge;

import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import cc.zyycc.bk.mixin.mc.concurrent.ThreadTaskExecutorMixin;
import cc.zyycc.bk.util.DrainLock;
import cc.zyycc.common.bridge.BridgeHolder;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import joptsimple.OptionSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.NetworkSystem;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.*;
import net.minecraftforge.common.MinecraftForge;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class MinecraftServerMixin extends ThreadTaskExecutorMixin implements MinecraftServerBridge, SimpleBridge {

    @Shadow
    @Final
    protected NetworkSystem networkSystem;

    @Shadow
    private int tickCounter;
    @Shadow
    protected long serverTime;
    @Shadow
    private IProfiler profiler;

    @Shadow
    protected abstract void func_240794_aZ_();

    @Shadow(remap = false)
    @Deprecated
    public abstract void markWorldsDirty();

    public OptionSet options;
    private boolean hasStopped = false;

    private Object stopLock;
    public CraftServer server;
    public ConsoleCommandSender console;
    public RemoteConsoleCommandSender remoteConsole;
    public ConsoleReader reader;
    public Commands vanillaCommandDispatcher;
    public Queue<Runnable> processQueue;

    @Final
    @Shadow
    protected IServerConfiguration serverConfig;

    @Shadow
    private static void func_240786_a_(ServerWorld p_240786_0_, IServerWorldInfo p_240786_1_, boolean hasBonusChest, boolean p_240786_3_, boolean p_240786_4_) {
    }

    public DatapackCodec datapackconfiguration;
    @Final
    @Shadow
    public final Map<RegistryKey<World>, ServerWorld> worlds = Maps.newLinkedHashMap();

    @Shadow
    protected abstract void func_240778_a_(IServerConfiguration p_240778_1_);

    private boolean forceTicks;

    private static MinecraftServer getServer() {
        return (Bukkit.getServer() instanceof CraftServer) ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At("HEAD"))
    public void updateTimeLightAndEntities(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.profiler.startSection("scheduler");//统计命令执行时间
        this.server.getScheduler().mainThreadHeartbeat(this.tickCounter);
        this.profiler.endStartSection("levels");
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Thread serverThread, DynamicRegistries.Impl dynamicRegistries, SaveFormat.LevelSave anvilConverterForAnvilFile, IServerConfiguration serverConfig, ResourcePackList dataPacks, Proxy serverProxy, DataFixer dataFixer, DataPackRegistries dataRegistries, MinecraftSessionService sessionService, GameProfileRepository profileRepo, PlayerProfileCache profileCache, IChunkStatusListenerFactory chunkStatusListenerFactory, CallbackInfo ci) {
        try {
            this.processQueue = new ConcurrentLinkedQueue<>();
            this.stopLock = new Object();

//            org.bukkit.plugin.java.PluginClassLoader
            this.options = BridgeHolder.options;

            this.reader = new ConsoleReader(System.in, System.out);
            //net/minecraft/server/v1_16_R3/DataPackConfiguration
            // net/minecraft/util/datafix/codec/DatapackCodec
//
//            this.datapackconfiguration = datapackconfiguration;
            //net/minecraft/server/v1_16_R3/DataPackResources net/minecraft/resources/DataPackRegistries
            //net/minecraft/server/v1_16_R3/CommandDispatcher net/minecraft/command/Commands
            this.datapackconfiguration = cc.zyycc.bk.bridge.server.SimpleBridge.datapackCodec;
            this.vanillaCommandDispatcher = dataRegistries.getCommandManager();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Inject(method = "func_240787_a_", at = @At("RETURN"))
    public void func_240787_a_(IChunkStatusListener p_240787_1_, CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        //this.serverConnection.acceptConnections();
    }


//    @Inject(method = "func_240787_a_", at = @At(value = "INVOKE", remap = false, target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER))
//    public void loadWorld(IChunkStatusListener p_240787_1_, CallbackInfo ci) {
//
//
//    }

    @Inject(method = "func_240787_a_", at = @At(value = "INVOKE", remap = false, target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void loadWorld(IChunkStatusListener p_240787_1_, CallbackInfo ci, IServerWorldInfo iserverworldinfo, DimensionGeneratorSettings dimensiongeneratorsettings, boolean flag, long i, long j, List list, SimpleRegistry simpleregistry, Dimension dimension, net.minecraft.world.gen.ChunkGenerator chunkgenerator, DimensionType dimensiontype, ServerWorld serverworld) {
        ((WorldBridge) serverworld).bridge$getWorld();
        ServerWorld serverWorld = worlds.get(World.OVERWORLD);
        this.server.scoreboardManager = new CraftScoreboardManager((MinecraftServer) (Object) this, serverWorld.getScoreboard());
    }

//    @Inject(method = "func_240787_a_", at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/world/border/WorldBorder;addListener(Lnet/minecraft/world/border/IBorderListener;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
//    public void loadWorld2(IChunkStatusListener p_240787_1_, CallbackInfo ci, IServerWorldInfo iserverworldinfo, DimensionGeneratorSettings dimensiongeneratorsettings, boolean flag, long i, long j, List list, SimpleRegistry simpleregistry, Dimension dimension, net.minecraft.world.gen.ChunkGenerator chunkgenerator, DimensionType dimensiontype, ServerWorld serverworld, DimensionSavedDataManager dimensionsaveddatamanager, WorldBorder worldborder, Iterator var17, Map.Entry entry, RegistryKey registrykey, RegistryKey registrykey1, DimensionType dimensiontype1, net.minecraft.world.gen.ChunkGenerator chunkgenerator1, DerivedWorldInfo derivedworldinfo, ServerWorld serverworld1) {
//        ((WorldBridge) serverworld1).bridge$getWorld();
//    }


    public RemoteConsoleCommandSender bridge$getRemoteConsole() {
        return remoteConsole;
    }

    public CommandSender getBukkitSender(CommandSource wrapper) {
        return this.console;
    }

    public void bridge$setConsole(ConsoleCommandSender console) {
        this.console = console;
    }

    public CommandSender bridge$getBukkitSender(CommandSource commandSource) {
        return console;
    }


    @Override
    public boolean bridge$hasStopped() {
        return hasStopped;
    }

    public final boolean hasStopped() {
        synchronized (stopLock) {
            return hasStopped;
        }
    }

    @Inject(method = "stopServer", cancellable = true, at = @At("HEAD"))
    public void stopServer(CallbackInfo ci) {
        synchronized (stopLock) {
            if (hasStopped) {
                ci.cancel();
                return;
            }
            hasStopped = true;
        }
    }

    public boolean isDebugging() {
        return false;
    }

    @Override
    public CraftServer bridge$getCraftServer() {
        return server;
    }

    public void initWorld(ServerWorld serverWorld, IServerWorldInfo serverWorldInfo, IServerConfiguration saveData, DimensionGeneratorSettings generatorSettings) {
        waitInitWorld(serverWorld, serverWorldInfo, saveData, generatorSettings);
//        new Thread(() -> {
//            try {
//                DrainLock.latch.await();
//
//                DrainLock.mainThreadQueue.add(() -> {
//
//                });
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }).start();


    }


    public void waitInitWorld(ServerWorld serverWorld, IServerWorldInfo serverWorldInfo, IServerConfiguration saveData, DimensionGeneratorSettings generatorSettings) {
        System.out.println("开始initWorld");
        boolean flag = generatorSettings.hasDebugChunkGenerator();
        ChunkGenerator chunkGenerator = ((WorldBridge) serverWorld).bridge$getGenerator();
        if (chunkGenerator != null) {
            ((WorldBridge) serverWorld).bridge$getWorld().getPopulators().
                    addAll(chunkGenerator.getDefaultPopulators(((WorldBridge) serverWorld).bridge$getWorld()));
        }

        WorldBorder worldborder = serverWorld.getWorldBorder();
        worldborder.deserialize(serverWorldInfo.getWorldBorderSerializer());
        this.server.getPluginManager().callEvent(new WorldInitEvent(((WorldBridge) serverWorld).bridge$getWorld()));
        if (!serverWorldInfo.isInitialized()) {
            try {
                func_240786_a_(serverWorld, serverWorldInfo, generatorSettings.hasBonusChest(), flag, true);
                serverWorldInfo.setInitialized(true);
                if (flag) {
                    this.func_240778_a_(this.serverConfig);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

                try {
                    serverWorld.fillCrashReport(crashreport);
                } catch (Throwable ignored) {
                }
                throw new ReportedException(crashreport);
            }
            serverWorldInfo.setInitialized(true);
        }
    }

    public void loadSpawn(IChunkStatusListener listener, ServerWorld serverWorld) {
        waitLoadSpawn(listener, serverWorld);
//        new Thread(() -> {
//            try {
//                DrainLock.latch.await();
//
//                DrainLock.mainThreadQueue.add(() -> {
//                    waitLoadSpawn(listener, serverWorld);
//                });
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }).start();


    }

    private void waitLoadSpawn(IChunkStatusListener listener, ServerWorld serverWorld) {
        System.out.println("开始loadSpawn");
        this.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(serverWorld));
        if (((WorldBridge) serverWorld).bridge$getWorld().getKeepSpawnInMemory()) {
            this.forceTicks = true;
            //LOGGER.info("Preparing start region for dimension {}", worldserver.getDimensionKey().a());
            BlockPos blockPos = serverWorld.getSpawnPoint();
            listener.start(new ChunkPos(blockPos));
            ServerChunkProvider chunkProvider = serverWorld.getChunkProvider();
            chunkProvider.getLightManager().func_215598_a(500);
            this.serverTime = Util.milliTime();


            chunkProvider.registerTicket(TicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);

            while (chunkProvider.getLoadedChunksCount() != 441) {

                this.executeModerately();
            }
            this.executeModerately();
            ServerWorld serverWorld1 = serverWorld;
            ForcedChunksSaveData forcedchunk = serverWorld.getSavedData().get(ForcedChunksSaveData::new, "chunks");
            if (forcedchunk != null) {
                LongIterator longiterator = forcedchunk.getChunks().iterator();

                while (longiterator.hasNext()) {
                    long i = longiterator.nextLong();
                    ChunkPos chunkPos = new ChunkPos(i);
                    serverWorld1.getChunkProvider().forceChunk(chunkPos, true);
                }
                net.minecraftforge.common.world.ForgeChunkManager.reinstatePersistentChunks(serverWorld, forcedchunk);
            }

            this.executeModerately();
            listener.stop();
            chunkProvider.getLightManager().func_215598_a(5);
//            serverWorld.setAllowedSpawnTypes(this.getSpawnMonsters(), this.getSpawnAnimals());
            this.func_240794_aZ_();
            this.forceTicks = false;
        }

    }


    private void executeModerately() {
        this.drainTasks();
        LockSupport.parkNanos("executing tasks", 1000L);
    }


    @Inject(method = "loadInitialChunks", at = @At("RETURN"))
    public void loadInitialChunks(IChunkStatusListener p_213186_1_, CallbackInfo ci) {
        DrainLock.latch.countDown();
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        Runnable task;
        while ((task = DrainLock.mainThreadQueue.poll()) != null) {
            task.run();
        }
    }

    @Inject(method = "isAheadOfTime", cancellable = true, at = @At("HEAD"))
    private void isAheadOfTime(CallbackInfoReturnable<Boolean> cir) {
        if (this.forceTicks) {
            cir.setReturnValue(true);
        }
    }


    @Override
    public void bridge$queuedProcess(Runnable runnable) {
        processQueue.add(runnable);
    }

}
