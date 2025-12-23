package cc.zyycc.bk.mixin.mc.world.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.storage.PlayerData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileInputStream;

@Mixin(PlayerData.class)
public class PlayerDataMixin {

    @Shadow @Final
    private File playerDataFolder;
    @Shadow @Final private static Logger LOGGER;

    public File getPlayerDir() {
        return this.playerDataFolder;
    }

    public CompoundNBT getPlayerData(String s) {
        try {
            File file1 = new File(this.playerDataFolder, s + ".dat");
            if (file1.exists()) {
                return CompressedStreamTools.readCompressed(new FileInputStream(file1));
            }
        } catch (Exception var3) {
            LOGGER.warn("Failed to load player data for " + s);
        }

        return null;
    }
}
