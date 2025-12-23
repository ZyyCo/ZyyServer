package cc.zyycc.bk.mixin.mc.world.storage;

import cc.zyycc.bk.bridge.world.storage.SaveFormat$LeaveSaveBridge;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

@Mixin(net.minecraft.world.storage.SaveFormat.LevelSave.class)
public class SaveFormat$LevelSaveMixin implements SaveFormat$LeaveSaveBridge {
    @Shadow
    @Final
    private Path saveDir;
    private RegistryKey<Dimension> dimensionType;
    @Override
    public void bridge$setDimType(RegistryKey<Dimension> typeKey) {
        this.dimensionType = typeKey;
    }

    @Inject(method = "getDimensionFolder", cancellable = true, at = @At("HEAD"))
    private void getDimensionFolder(RegistryKey<World> dimensionKey, CallbackInfoReturnable<File> cir) {
        if (dimensionType == Dimension.OVERWORLD) {
            cir.setReturnValue(this.saveDir.toFile());
        } else if (dimensionType == Dimension.THE_NETHER) {
            cir.setReturnValue(new File(this.saveDir.toFile(), "DIM-1"));
        } else if (dimensionType == Dimension.THE_END) {
            cir.setReturnValue(new File(this.saveDir.toFile(), "DIM1"));
        }
    }
}
