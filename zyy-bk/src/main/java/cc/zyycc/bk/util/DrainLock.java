package cc.zyycc.bk.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class DrainLock {
    public static final CountDownLatch latch = new CountDownLatch(1);

    public  static final   Queue<Runnable> mainThreadQueue = new ConcurrentLinkedQueue<>();
}
