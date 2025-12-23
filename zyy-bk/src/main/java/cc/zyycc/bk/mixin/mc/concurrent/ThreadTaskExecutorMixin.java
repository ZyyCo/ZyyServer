package cc.zyycc.bk.mixin.mc.concurrent;


import net.minecraft.util.concurrent.ThreadTaskExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThreadTaskExecutor.class)
public abstract class ThreadTaskExecutorMixin<R extends Runnable> {
    @Shadow
    protected abstract void drainTasks();
}
