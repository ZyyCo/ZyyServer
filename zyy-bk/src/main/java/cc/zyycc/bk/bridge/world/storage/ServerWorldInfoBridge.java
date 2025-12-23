package cc.zyycc.bk.bridge.world.storage;

import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.IServerWorldInfo;

public interface ServerWorldInfoBridge {
    WorldSettings bridge$getWorldSettings();

    String bridge$getBKWorldName(World world);

    void bridge$setBKWorldName(String name);

    void bridge$setWorldName(String name);

    boolean bridge$isBKCreated();
}
