package cc.zyycc.bk.mixin.bk;

import cc.zyycc.bk.bridge.server.JavaPluginLoaderBridge;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = JavaPluginLoader.class, remap = false)
public abstract class JavaPluginLoaderMixin implements JavaPluginLoaderBridge {


    @Inject(method = "enablePlugin", at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/java/JavaPlugin;setEnabled(Z)V", shift = At.Shift.AFTER), remap = false)
    public void enablePlugin(Plugin plugin, CallbackInfo ci) {
        System.out.println("当前插件" + plugin.getName());
        if (!plugin.isEnabled()) {
            System.err.println("========== 插件未成功启用（静默失败） ==========");
            System.err.println("插件：" + plugin.getName());
            System.err.println("主类：" + plugin.getDescription().getMain());
            System.err.println("版本：" + plugin.getDescription().getVersion());
            System.err.println("原因：插件在 onEnable() 内部提前 return 或 setEnabled(false)");
            System.err.println("==============================================");

            new Exception("插件未成功启用 but no error").printStackTrace();
        }

    }

}
