package cc.zyycc.bk.bridge.world.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;

public interface IChunkManagerBridge {
    ChunkManager$CallbackExecutor bridge$getCallbackExecutor();


    Long2ObjectLinkedOpenHashMap<ChunkHolder> bridge$getImmutableLoadedChunks();
}
