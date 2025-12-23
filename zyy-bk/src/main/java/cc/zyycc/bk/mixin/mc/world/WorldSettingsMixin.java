package cc.zyycc.bk.mixin.mc.world;

import cc.zyycc.bk.bridge.world.WorldSettingsBridge;
import cc.zyycc.bk.bridge.world.storage.DerivedWorldInfoBridge;
import cc.zyycc.bk.util.BKWorldFileBridge;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.DerivedWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;



@Mixin(WorldSettings.class)
public abstract class WorldSettingsMixin implements WorldSettingsBridge {
    @Mutable
    @Final
    @Shadow
    private String worldName;

    @Override
    public String bridge$getBKWorldName(World world) {
        if (world.worldInfo instanceof DerivedWorldInfo) {
            return ((DerivedWorldInfoBridge) world.worldInfo).bridge$getBKWorldName();
        }
        if(worldName == null){
            return "world";
        }
        return worldName;
    }

    @Override
    public void bridge$setWorldName(String name) {
        this.worldName = name;
    }
}