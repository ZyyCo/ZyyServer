package cc.zyycc.bk.mixin.mc.world.server;

import net.minecraft.world.server.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkManager.EntityTracker.class)
public class ChunkManagerMixin {



    @Inject(method = "updateTrackingState", at = @At("HEAD"))
    public void updateTrackingState(int x, int y, int z, CallbackInfo ci) {

    }
}
