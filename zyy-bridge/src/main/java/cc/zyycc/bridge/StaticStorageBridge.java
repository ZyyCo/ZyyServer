package cc.zyycc.bridge;



public class StaticMapStorageBridge<C> extends AbstractBridge<Object> {

    private final String fieldName;
    private final C pluginLoaders;


    public StaticMapStorageBridge(String className,  String filedName) {
        super(className, ClassLoader.getSystemClassLoader());
        this.pluginLoaders = getStaticField(filedName);
        this.fieldName = filedName;
    }



    public void<T,V> register(T t, V v) {
        pluginLoaders.put(t, v);
    }

    public C get() {
        return getStaticField(fieldName);
    }
}
