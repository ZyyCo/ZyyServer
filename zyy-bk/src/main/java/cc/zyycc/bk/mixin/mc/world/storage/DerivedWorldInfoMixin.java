package cc.zyycc.bk.mixin.mc.world.storage;

import cc.zyycc.bk.bridge.world.storage.DerivedWorldInfoBridge;
import cc.zyycc.bk.bridge.world.storage.ServerWorldInfoBridge;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import org.spongepowered.asm.mixin.*;

@Mixin(DerivedWorldInfo.class)
public abstract class DerivedWorldInfoMixin implements DerivedWorldInfoBridge,ServerWorldInfoBridge {
    @Final
    @Shadow
    private IServerConfiguration configuration;
    @Final
    @Shadow
    private IServerWorldInfo delegate;

    private String bkWorldName;
    @Override
    public void bridge$setBKWorldName(String name) {
        this.bkWorldName = name;
    }
    @Override
    public String bridge$getBKWorldName() {
        if (this.bkWorldName == null) {
            this.bkWorldName = this.configuration.getWorldName();
        }
        return this.bkWorldName;
    }

//    @Overwrite
//    public void setRainTime(int time) {
//        delegate.setRainTime(time);
//    }


}