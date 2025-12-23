package cc.zyycc.bk.mixin.core.util.text;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(net.minecraft.util.text.Style.class)
public class StyleMixin {
    @Shadow @Final
    private Color color;
    @Shadow @Final
    private Boolean bold;
    @Shadow @Final
    private Boolean italic;
    @Shadow @Final
    private Boolean strikethrough;
    @Shadow @Final
    private Boolean obfuscated;
    @Shadow @Final
    private Boolean underlined;
    @Shadow @Final
    private ClickEvent clickEvent;
    @Shadow @Final
    private HoverEvent hoverEvent;
    @Shadow @Final
    private String insertion;
    @Shadow @Final
    private ResourceLocation fontId;

    public Style setUnderline(@Nullable Boolean obool) {
        Style style = Style.EMPTY;
        style.setUnderlined(obool);
        return style;
    }


}
