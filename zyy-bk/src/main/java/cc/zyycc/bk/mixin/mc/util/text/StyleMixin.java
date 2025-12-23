package cc.zyycc.bk.mixin.mc.util.text;

import net.minecraft.command.Commands;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
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
        return new Style(this.color, this.bold, this.italic, obool, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setRandom(@Nullable Boolean obool) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obool, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setStrikethrough(@Nullable Boolean obool) {
        return new Style(this.color, this.bold, this.italic, this.underlined, obool, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setChatClickable(@Nullable ClickEvent chatclickable) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, chatclickable, this.hoverEvent, this.insertion, this.fontId);
    }

    public Style setChatHoverable(@Nullable HoverEvent chathoverable) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, chathoverable, this.insertion, this.fontId);
    }



}
