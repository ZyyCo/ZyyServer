package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.world.server.ChunkManager$CallbackExecutor;
import cc.zyycc.bk.bridge.world.server.IChunkManagerBridge;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.world.server.ChunkHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(net.minecraft.world.server.ChunkManager.class)
public class ChunkManagerMixin implements IChunkManagerBridge {
    @Shadow
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> immutableLoadedChunks;


    public final ChunkManager$CallbackExecutor callbackExecutor = new ChunkManager$CallbackExecutor();


    @Override
    public ChunkManager$CallbackExecutor bridge$getCallbackExecutor() {
        return callbackExecutor;
    }

    @Override
    public Long2ObjectLinkedOpenHashMap<ChunkHolder> bridge$getImmutableLoadedChunks() {
        return immutableLoadedChunks;
    }
}
