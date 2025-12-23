package cc.zyycc.bk.bridge.server.management;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.login.ServerLoginNetHandler;

import java.net.SocketAddress;

public interface PlayerListBridge {
    ServerPlayerEntity bridge$canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile, ServerLoginNetHandler handler);
}
