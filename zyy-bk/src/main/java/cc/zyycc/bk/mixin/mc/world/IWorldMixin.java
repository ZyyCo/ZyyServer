package cc.zyycc.bk.mixin.mc.world;

import cc.zyycc.bk.bridge.SimpleBridge;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IWorld.class)
public interface IWorldMixin extends SimpleBridge {
    default ServerWorld getMinecraftWorld() {
        return this.bridge$getMinecraftWorld();
    }
}
