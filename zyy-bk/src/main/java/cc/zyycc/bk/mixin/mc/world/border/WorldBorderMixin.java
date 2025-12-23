package cc.zyycc.bk.mixin.mc.world.border;

import cc.zyycc.bk.bridge.world.border.WorldBorderBridge;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldBorder.class)
public class WorldBorderMixin implements WorldBorderBridge {
    public World world;
    @Override
    public void bridge$setWorld(World world) {
        this.world = world;
    }

    @Override
    public World bridge$getWorld() {
        return world;
    }
}
