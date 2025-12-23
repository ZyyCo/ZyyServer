package cc.zyycc.bk.bridge.world.storage;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.Dimension;

public interface SaveFormat$LeaveSaveBridge {
    void bridge$setDimType(RegistryKey<Dimension> typeKey);
}
