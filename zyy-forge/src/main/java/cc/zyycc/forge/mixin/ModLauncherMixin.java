package cc.zyycc.forge.mixin;

import cpw.mods.modlauncher.Launcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Launcher.class)
public abstract class ModLauncherMixin {
    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void run(String[] args, CallbackInfo ci) {
        System.out.println("启动ModLauncherMixin.run");
    }
}
