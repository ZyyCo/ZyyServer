package cc.zyycc.bk.bridge.world.server;

import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorldLightManager;

public interface ServerChunkProviderBridge {
    ServerChunkProvider bridge$getServerChunkProvider();

    boolean bridge$tickDistanceManager();

    public ServerWorldLightManager bridge$getLightManager();
}
