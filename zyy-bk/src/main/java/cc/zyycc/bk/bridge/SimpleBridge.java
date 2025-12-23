package cc.zyycc.bk.bridge;

import net.minecraft.world.server.ServerWorld;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

public interface SimpleBridge {

    ServerWorld bridge$getMinecraftWorld();
    CraftServer bridge$getCraftServer();

}
