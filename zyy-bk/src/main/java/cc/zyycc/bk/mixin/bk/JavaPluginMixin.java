package cc.zyycc.bk.mixin.bk;

import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = org.bukkit.plugin.java.JavaPlugin.class, remap = false)
public abstract class JavaPluginMixin {
    @Shadow
    public abstract void onEnable();
    @Shadow
    public abstract void onDisable();

    @Inject(method = "onEnable", at = @At("HEAD"))
    private void zyy_onEnableStart(CallbackInfo ci) {
        JavaPlugin plugin = (JavaPlugin)(Object)this;
        System.err.println("===> [DEBUG] 开始执行 onEnable(): " + plugin.getName());
    }

    @Inject(method = "onEnable", at = @At("RETURN"))
    private void zyy_onEnableEnd(CallbackInfo ci) {
        JavaPlugin plugin = (JavaPlugin)(Object)this;
        if (!plugin.isEnabled()) {
            System.err.println("===> [ERROR] 插件内部 onEnable() 执行结束但 disabled！插件内部静默失败: " + plugin.getName());
            new Exception("插件内部 onEnable() silent fail").printStackTrace();
        } else {
            System.err.println("===> [DEBUG] 成功退出 onEnable(): " + plugin.getName());
        }
    }

    @Inject(method = "setEnabled", at = @At("RETURN"))
    public void zyy_afterSetEnabled(boolean enabled, CallbackInfo ci) {
        JavaPlugin plugin = (JavaPlugin)(Object)this;
        if (enabled && !plugin.isEnabled()) {
            System.err.println("[DEBUG] 插件内部静默失败：" + plugin.getName());
            new Exception("插件内部 setEnabled(false) or return").printStackTrace();
        }
    }

}
