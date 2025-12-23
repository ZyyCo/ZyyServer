package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.world.server.IChunkManagerBridge;
import cc.zyycc.bk.bridge.world.server.ServerChunkProviderBridge;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.server.ServerChunkProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.server.ServerChunkProvider$ChunkExecutor")
public abstract class ServerChunkProvider$ChunkExecutorMixin extends ThreadTaskExecutor<Runnable> {
    @Shadow(aliases = {"this$0", "field_213181_a"}, remap = false)
    @Final
    private ServerChunkProvider serverChunkProvider;

    protected ServerChunkProvider$ChunkExecutorMixin(String p_i50403_1_) {
        super(p_i50403_1_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    protected boolean driveOne() {
        ServerChunkProviderBridge provider = (ServerChunkProviderBridge) serverChunkProvider;
        try {
            if (!provider.bridge$tickDistanceManager()) {//func_217235_l
                provider.bridge$getLightManager().func_215588_z_();
                return super.driveOne();
            }
        } finally {
            ((IChunkManagerBridge) serverChunkProvider.chunkManager).bridge$getCallbackExecutor().run();
        }
        return true;
    }
}
