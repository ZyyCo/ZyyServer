package cc.zyycc.bk.mixin.mc.util;

import cc.zyycc.bk.bridge.WorldBridge;
import cc.zyycc.bk.bridge.mc.IWorldPosCallableBridge;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;
import java.util.function.BiFunction;

@Mixin(net.minecraft.util.IWorldPosCallable.class)
public interface IWorldPosCallableMixin {

    default World getWorld() {
        System.out.println("知道了IWorldPosCallableMixin getWorld");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default BlockPos getPosition() {
        System.out.println("知道了IWorldPosCallableMixin getPosition");
        throw new UnsupportedOperationException("Not supported yet.");
    }


    default Location getLocation() {
        System.out.println("知道了IWorldPosCallableMixin getLocation");
        return new Location(((WorldBridge) this.getWorld()).bridge$getWorld(),
                (double) this.getPosition().getX(), (double) this.getPosition().getY(), (double) this.getPosition().getZ());
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    static IWorldPosCallable of(final World world, final BlockPos pos) {
         class $IWorldPosCallable implements net.minecraft.util.IWorldPosCallable, IWorldPosCallableBridge {
             @Override
             public World bridge$getWorld() {
                 return world;
             }

             @Override
             public BlockPos bridge$getPosition() {
                 return pos;
             }

             @Override
             public Location bridge$getLocation() {
                 return IWorldPosCallableBridge.super.bridge$getLocation();
             }

             @Override
             public <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer) {
                 return Optional.of(worldPosConsumer.apply(world, pos));
             }
         }

        return new $IWorldPosCallable();


    }
}
