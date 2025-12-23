package cc.zyycc.bk.mixin.mc.concurrent;

import net.minecraft.util.concurrent.RecursiveEventLoop;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecursiveEventLoop.class)
public abstract class RecursiveEventLoopMixin<R extends Runnable> extends ThreadTaskExecutorMixin<R> {
}
