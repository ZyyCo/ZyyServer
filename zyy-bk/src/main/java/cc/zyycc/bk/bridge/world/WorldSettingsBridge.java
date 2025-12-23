package cc.zyycc.bk.bridge.world;

import net.minecraft.world.World;

public interface WorldSettingsBridge {

    String bridge$getBKWorldName(World world);

    void bridge$setWorldName(String name);
}
