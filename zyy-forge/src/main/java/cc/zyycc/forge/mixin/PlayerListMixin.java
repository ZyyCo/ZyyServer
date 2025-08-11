package cc.zyycc.forge.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.storage.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void PlayerListMixinCode(MinecraftServer minecraftServer, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_, CallbackInfo ci) {
        System.out.println("MIXIN注入成功");
    }
}