package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.world.ChunkBridge;
import cc.zyycc.bk.bridge.world.server.ChunkHolderBridge;
import cc.zyycc.bk.bridge.world.server.ChunkManagerBridge;
import cc.zyycc.bk.bridge.world.server.IChunkManagerBridge;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(net.minecraft.world.server.ChunkHolder.class)
public abstract class ChunkHolderMixin implements ChunkHolderBridge {
    @Shadow
    public abstract CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219301_a(ChunkStatus p_219301_1_);

    @Shadow
    private int prevChunkLevel;

    @Override
    public Chunk bridge$getFullChunk() {
        return this.getFullChunkUnchecked();
    }

    public Chunk getFullChunk() {
        return !ChunkHolder.getLocationTypeFromLevel(this.prevChunkLevel)
                .isAtLeast(ChunkHolder.LocationType.BORDER) ? null : this.getFullChunkUnchecked();
    }

    public Chunk getFullChunkUnchecked() {
        CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> statusFuture = this.func_219301_a(ChunkStatus.FULL);
        Either<IChunk, ChunkHolder.IChunkLoadingError> either = (Either) statusFuture.getNow(null);
        return either == null ? null : (Chunk) either.left().orElse(null);
    }

    @Inject(method = "processUpdates", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void inject_processUpdates(ChunkManager chunkManagerIn, CallbackInfo ci,
                                       ChunkStatus chunkStatus, ChunkStatus chunkStatus1, boolean flag, boolean flag1,
                                       ChunkHolder.LocationType chunkholder$locationtype, ChunkHolder.LocationType chunkholder$locationtype1) {
        if (chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.BORDER) && !chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.BORDER)) {
            this.func_219301_a(ChunkStatus.FULL).thenAccept((eitherx) -> {//玩家看不到/离开这个区块
                Chunk chunk = (Chunk) eitherx.left().orElse(null);
                if (chunk != null) {
                    ((IChunkManagerBridge) chunkManagerIn).bridge$getCallbackExecutor().execute(() -> {
                        chunk.setModified(true);
                        ((ChunkBridge) chunk).bridge$unloadCallback();
                    });
                }

            }).exceptionally((throwable) -> {
                // System.err.println("Failed to schedule unload callback for chunk " + this.location);
                //    MinecraftServer.LOGGER.fatal("Failed to schedule unload callback for chunk " + this.location, throwable);
                return null;
            });
            ((IChunkManagerBridge) chunkManagerIn).bridge$getCallbackExecutor().run();
        }
    }


    @Inject(method = "processUpdates", at = @At(value = "RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void inject_processUpdates2(ChunkManager chunkManagerIn, CallbackInfo ci,
                                        ChunkStatus chunkStatus, ChunkStatus chunkStatus1,
                                        boolean flag, boolean flag1,
                                        ChunkHolder.LocationType chunkholder$locationtype, ChunkHolder.LocationType chunkholder$locationtype1) {
        if (!chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.BORDER) && chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.BORDER)) {
            this.func_219301_a(ChunkStatus.FULL).thenAccept((eitherx) -> {
                Chunk chunk = (Chunk) eitherx.left().orElse(null);
                if (chunk != null) {
                    ((IChunkManagerBridge) chunkManagerIn).bridge$getCallbackExecutor().execute(((ChunkBridge) chunk)::bridge$loadCallback);
                }

            }).exceptionally((throwable) -> {
        //        MinecraftServer.LOGGER.fatal("Failed to schedule load callback for chunk " + this.location, throwable);
                return null;
            });
            ((IChunkManagerBridge) chunkManagerIn).bridge$getCallbackExecutor().run();
        }
    }
}
