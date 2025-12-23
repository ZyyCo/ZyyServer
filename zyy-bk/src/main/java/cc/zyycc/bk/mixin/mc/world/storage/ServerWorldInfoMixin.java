package cc.zyycc.bk.mixin.mc.world.storage;

import cc.zyycc.bk.bridge.world.WorldSettingsBridge;
import cc.zyycc.bk.bridge.world.storage.DerivedWorldInfoBridge;
import cc.zyycc.bk.bridge.world.storage.ServerWorldInfoBridge;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.world.storage.ServerWorldInfo.class)
public class ServerWorldInfoMixin implements ServerWorldInfoBridge {
    @Shadow
    private net.minecraft.world.WorldSettings worldSettings;
    @Final
    @Shadow
    private DimensionGeneratorSettings generatorSettings;

    public boolean isBKCreated;

    public boolean bridge$isBKCreated() {
        return isBKCreated;
    }

    public ServerWorldInfo bkCreate() {
        isBKCreated = true;
        return (net.minecraft.world.storage.ServerWorldInfo) (Object) this;
    }

    public void checkName(String name) {//bukkit checkName
        if (!worldSettings.getWorldName().equals(name)) {
            this.bridge$setBKWorldName(name);
        }
    }

    @Override
    public WorldSettings bridge$getWorldSettings() {
        return worldSettings;
    }

    @Override
    public String bridge$getBKWorldName(World world) {
        IServerWorldInfo info = (IServerWorldInfo) world.worldInfo;
        if (info instanceof DerivedWorldInfo) {
            return ((DerivedWorldInfoBridge) info).bridge$getBKWorldName();
        }
        return ((WorldSettingsBridge) worldSettings).bridge$getBKWorldName(world);
    }

    @Override
    public void bridge$setBKWorldName(String name) {
        ((WorldSettingsBridge) worldSettings).bridge$setWorldName(name);
    }

    @Override
    public void bridge$setWorldName(String name) {
        ((WorldSettingsBridge) worldSettings).bridge$setWorldName(name);
    }


}
