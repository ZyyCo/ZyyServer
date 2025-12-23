package cc.zyycc.bk.mixin.util.concurrent;

import net.minecraft.util.concurrent.RecursiveEventLoop;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecursiveEventLoop.class)
public abstract class RecursiveEventLoopMixin extends ThreadTaskExecutorMixin{
}
