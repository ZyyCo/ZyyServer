package cc.zyycc.bridge;


import cc.zyycc.common.VersionInfo;

public class VersionInfoBridge extends AbstractBridge<VersionInfo> {


    public VersionInfoBridge() {
        super("cc.zyycc.common.VersionInfo", ClassLoader.getSystemClassLoader());
    }
}
