package cc.zyycc.common.bridge.loader;

public enum MyLoader {
    ZYY("zyyLoader"),
    BK("bkLoader"),
    AGENT("agentLoader"),
    Transforming("transforming");

    private final String name;

    MyLoader(String bkLoader) {
        this.name = bkLoader;
    }

    public ClassLoader classLoader()  {
        return LoaderManager.getClassLoader(this);
    }

    public String getName() {
        return name;
    }
}
