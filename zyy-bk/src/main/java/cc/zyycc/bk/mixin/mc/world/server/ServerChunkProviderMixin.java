package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.world.server.ChunkHolderBridge;
import cc.zyycc.bk.bridge.world.server.ServerChunkProviderBridge;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorldLightManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin implements ServerChunkProviderBridge {
    @Shadow
    @Final
    public ChunkManager chunkManager;

    @Final
    @Shadow
    private ServerWorldLightManager lightManager;

    @Shadow
    protected abstract boolean func_217235_l();

    public boolean isChunkLoaded(final int chunkX, final int chunkZ) {
        long aLong = ChunkPos.asLong(chunkX, chunkZ);
        ChunkHolder chunk = this.chunkManager.func_219220_a(aLong);
        if (chunk == null) {
            return false;
        } else {
            return ((ChunkHolderBridge) chunk).bridge$getFullChunk() != null;
        }
    }

    @Override
    public boolean bridge$tickDistanceManager() {
        return func_217235_l();
    }

    @Override
    public ServerChunkProvider bridge$getServerChunkProvider() {
        return (ServerChunkProvider) (Object) this;
    }
    @Override
    public ServerWorldLightManager bridge$getLightManager() {
        return lightManager;
    }

}
