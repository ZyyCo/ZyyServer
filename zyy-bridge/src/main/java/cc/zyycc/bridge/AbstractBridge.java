package cc.zyycc.bridge;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractBridge<C> {
    private Class<?> clazz;
    private final ClassLoader loader;
    private final String className;
    private final Object[] args;
    private C instance;

    public AbstractBridge(String className, ClassLoader loader) {
        this(className, loader, new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public AbstractBridge(String className, ClassLoader loader, Object... args) {
        try {
            this.clazz = Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.className = className.replace("/", ".");
        this.loader = loader;
        this.args = args;
    }

    public AbstractBridge(Class<?> clazz, ClassLoader loader, Object... args) {
        this.clazz = clazz;
        this.className = clazz.getSimpleName();
        this.loader = loader;
        this.args = args;
    }


    public ClassLoader getLoader() {
        return loader;
    }

    public String getClassName() {
        return className;
    }

    public <S> S getStaticField(String... fieldName) {
        try {
            if (fieldName.length == 1) {
                return (S) clazz.getDeclaredField(fieldName[0]).get(null);
            } else {
                Class<?>[] types = new Class<?>[fieldName.length];
                for (int i = 0; i < fieldName.length; i++) {
                    types[i] = fieldName[i].getClass();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public <S> S getStaticField(String fieldName) {
        try {
            return (S) clazz.getDeclaredField(fieldName).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <S> S getStaticMethod(String methodName, Class<?>... parameterTypes) {
        try {
            return (S) clazz.getDeclaredMethod(methodName, parameterTypes).invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


    public C getInstance() {
        if (instance == null) {
            try {
                Class<?> clazz = Class.forName(className, true, loader);
                if (args.length == 0) {
                    this.instance = (C) clazz.getDeclaredConstructor().newInstance();
                } else {
                    Class<?>[] types = new Class<?>[args.length];
                    for (int i = 0; i < args.length; i++) {
                        types[i] = args[i].getClass();
                    }
                    this.instance = (C) clazz.getDeclaredConstructor(types).newInstance(args);
                }


            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    protected Object[] getArgs() {
        return args;
    }
}
