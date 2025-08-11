package cc.zyycc.common.bridge;

public class PluginLoaderBridge {
    public ClassLoader classLoader;
    protected PluginLoaderBridge(){
        this.classLoader = null;
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
