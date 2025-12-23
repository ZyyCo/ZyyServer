package cc.zyycc.bk.mixin.mc.util.text;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Color.class)
public class ColorMixin {
    public TextFormatting format;


    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    private void init(int color, String name, CallbackInfo ci) {
        this.format = TextFormatting.getValueByName(name);
    }
}
