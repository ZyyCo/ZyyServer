package cc.zyycc.bridge;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class StaticStorageBridge<C> extends AbstractBridge<Object> {

    private final String fieldName;
    private final C pluginLoaders;
    private String internal;


    public StaticStorageBridge(String className, String fieldName) {
        super(className, ClassLoader.getSystemClassLoader());
        this.fieldName = fieldName;
        this.pluginLoaders = (C) getStaticField(fieldName);
    }

    public StaticStorageBridge(String className, String fieldName, String internalField) {

        super(className, ClassLoader.getSystemClassLoader());
        this.fieldName = fieldName;
        this.internal = internalField;
        C c;
        if (internalField != null) {
            try {
                Field field = getClazz().getDeclaredField(fieldName);
                field.setAccessible(true);
                this.pluginLoaders = (C) field.get(internalField);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.pluginLoaders = (C) getStaticField(fieldName);
        }
    }

    public StaticStorageBridge(StaticStorageBridge<?> bridge, String filedName) {
        super(bridge.getClazz(), bridge.getLoader(), bridge.getArgs());
        this.fieldName = filedName;
        this.pluginLoaders = (C) getStaticField(fieldName);
    }

    @SuppressWarnings("unchecked")
    public <T> void add(T t) {
        ((Collection<T>) pluginLoaders).add(t);
    }

    @SuppressWarnings("unchecked")
    public <T, V> void put(T t, V v) {
        if (pluginLoaders instanceof Map) {
            ((Map<T, V>) pluginLoaders).put(t, v);
        } else {
            throw new IllegalStateException("The field is not a Map.");
        }
    }

    @SuppressWarnings("unchecked")
    public C get() {
        return (C) getStaticField(fieldName);
    }
}
