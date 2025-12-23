package cc.zyycc.bk.bridge.mc;

import cc.zyycc.bk.bridge.WorldBridge;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public interface IWorldPosCallableBridge {
    default World bridge$getWorld() {
        return ((IWorldPosCallable) this).apply((a, b) -> a).orElse(null);
    }

    default BlockPos bridge$getPosition() {
        return ((IWorldPosCallable) this).apply((a, b) -> b).orElse(null);
    }

    default Location bridge$getLocation() {
        return new Location(((WorldBridge) this.bridge$getWorld()).bridge$getWorld(),
                (double) this.bridge$getPosition().getX(), (double) this.bridge$getPosition().getY(), (double) this.bridge$getPosition().getZ());
    }

}
