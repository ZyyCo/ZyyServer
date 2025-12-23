package cc.zyycc.bk.mixin.mc.world.storage;

import cc.zyycc.bk.bridge.world.storage.SaveFormat$LeaveSaveBridge;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.Dimension;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(SaveFormat.class)
public abstract class SaveFormatMixin {
    @Shadow
    public abstract SaveFormat.LevelSave getLevelSave(String saveName) throws IOException;

    public SaveFormat.LevelSave getLevelSave(String saveName, RegistryKey<Dimension> world) throws IOException {
        SaveFormat.LevelSave save = getLevelSave(saveName);
        ((SaveFormat$LeaveSaveBridge) save).bridge$setDimType(world);
        return save;
    }

    public SaveFormat.LevelSave c(String s, RegistryKey<Dimension> dimensionType) throws IOException {
        return getLevelSave(s, dimensionType);
    }
}
