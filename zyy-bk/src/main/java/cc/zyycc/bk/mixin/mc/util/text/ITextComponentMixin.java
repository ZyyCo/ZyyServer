package cc.zyycc.bk.mixin.mc.util.text;

import cc.zyycc.bk.bridge.util.Text.ITextComponentBridge;
import com.google.common.collect.Streams;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Mixin(ITextComponent.class)
public interface ITextComponentMixin extends ITextComponentBridge , Iterable<ITextComponent> {
    @Shadow
    List<ITextComponent> getSiblings();


    default Stream<ITextComponent> stream() {
        return Streams.concat(Stream.of((ITextComponent) this), this.getSiblings().stream().flatMap(iTextComponent ->
                        ((ITextComponentBridge) iTextComponent).bridge$stream()

                ));
    }


    @Override
    default Stream<ITextComponent> bridge$stream() {
        return stream();
    }
    @Override
    default Iterator<ITextComponent> bridge$iterator() {
        return iterator();
    }

    @Override
    default Iterator<ITextComponent> iterator() {
        return this.stream().iterator();
    }


}
