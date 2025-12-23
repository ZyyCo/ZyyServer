package cc.zyycc.bk.mixin.util;

import java.util.concurrent.CountDownLatch;

public class drainLock {
    public static final CountDownLatch latch = new CountDownLatch(1);
}
