package cc.zyycc.common.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoaderManager {

    public static final Map<String, RegisterClassLoader> registerLoaders = new ConcurrentHashMap<>();


    public static ClassLoader createURLClassLoader(MyLoader name, List<Path> jarPath, ClassLoader parent) {
        try {

            URL[] urls = jarPath.stream()
                    .map(path -> {
                        try {
                            return path.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);
            URLClassLoader bkLoader = new URLClassLoader(urls, parent);
            setClassLoader(name, bkLoader);
            return bkLoader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setClassLoader(MyLoader name, ClassLoader classLoader) {
        registerLoaders.put(name.getName(), new RegisterClassLoader(classLoader));
    }

    public static ClassLoader getClassLoader(MyLoader loader) {
        return registerLoaders.computeIfAbsent(loader.getName(),
                name -> new RegisterClassLoader(ClassLoader.getSystemClassLoader())
        ).getClassLoader();

    }

}
