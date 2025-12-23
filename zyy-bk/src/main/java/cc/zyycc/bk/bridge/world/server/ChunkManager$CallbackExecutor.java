package cc.zyycc.bk.bridge.world.server;

import java.util.concurrent.Executor;

public class ChunkManager$CallbackExecutor implements Executor, Runnable  {
    private Runnable queued;
    @Override
    public void run() {
        Runnable task = this.queued;
        this.queued = null;
        if (task != null) {
            task.run();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.queued != null) {
            throw new IllegalStateException("Already queued");
        } else {
            this.queued = runnable;
        }
    }
}
