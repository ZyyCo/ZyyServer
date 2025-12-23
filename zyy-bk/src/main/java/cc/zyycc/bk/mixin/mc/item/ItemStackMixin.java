package cc.zyycc.bk.mixin.mc.item;

import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Final
    @Shadow
    private static  Logger LOGGER;
    public abstract CompoundNBT write(CompoundNBT nbt);
    public void convertStack(int version) {
        if (0 < version && version < CraftMagicNumbers.INSTANCE.getDataVersion()) {
            CompoundNBT savedStack = new CompoundNBT();
            savedStack = (CompoundNBT) ((CraftServer) Bukkit.getServer()).getServer()
                    .getDataFixer().update(TypeReferences.ITEM_STACK, new Dynamic(NBTDynamicOps.INSTANCE, savedStack), version, CraftMagicNumbers.INSTANCE.getDataVersion()).getValue();
            this.write(savedStack);
        }
    }
}
