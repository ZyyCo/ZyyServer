package cc.zyycc.bk.mixin.mc.world.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkManager.EntityTracker.class)
public class ChunkManager$EntityTrackerMixin {


    @Inject(method = "updateTrackingState(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At("HEAD"))
    public void updateTrackingState(ServerPlayerEntity entity, CallbackInfo ci) {
//        if (entity.getClass().getName().equals("net.citizensnpcs.nms.v1_16_R3.entity.EntityHumanNPC")) {
//            System.out.println("更新实体track来自Class" + entity.getClass().getName());
//        }
    }
}
