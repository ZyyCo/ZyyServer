package cc.zyycc.bk.bridge.network.play;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface ServerPlayNetHandlerBridge {
    boolean bridge$isDisconnected();

    void bridge$teleport(Location dest);

    void bridge$teleportCause(PlayerTeleportEvent.TeleportCause cause);
}
