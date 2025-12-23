package cc.zyycc.bk.bridge.world.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.server.ChunkManager;

import java.util.Map;

public class ChunkManagerBridge {
    public static Int2ObjectMap<ChunkManager.EntityTracker> getTrackedEntities(ChunkManager cm) {
        return cm.entities;
    }


    public ChunkManager$CallbackExecutor bridge$getCallbackExecutor();
}
