package cc.zyycc.bk.bridge.player;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

public interface ServerPlayerEntityBridge {
    CraftPlayer getBukkitEntity();

    void bridge$setCompassTarget(Location compassTarget);

    BlockPos bridge$getSpawnPoint(ServerWorld world);


    boolean bridge$isJoining();


    void bridge$reset();
}
