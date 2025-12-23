package cc.zyycc.bk.mixin.mc.world;

import net.minecraft.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.world.IWorldWriter.class)
public interface IWorldWriterMixin {

    default boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        System.out.println("知道了" + this);
        return false;
    }

}
