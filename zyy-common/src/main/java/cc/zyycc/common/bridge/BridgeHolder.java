package cc.zyycc.common.bridge;
public class BridgeHolder {


    public static PluginLoaderBridge INSTANCE = new PluginLoaderBridge();


    public static PluginLoaderBridge getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PluginLoaderBridge();
        }
        return INSTANCE;
    }

}
