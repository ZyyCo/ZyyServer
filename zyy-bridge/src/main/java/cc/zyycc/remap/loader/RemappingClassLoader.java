package cc.zyycc.remap.loader;

public class RemappingClassLoader extends ClassLoader{
    public RemappingClassLoader(ClassLoader parent) {
        super(parent);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
