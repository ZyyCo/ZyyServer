package cc.zyycc.common.bridge;

public class PreScanBridge {

    public static volatile boolean ready = false;
    public static void notifyReady() {
        ready = true;
    }
}
