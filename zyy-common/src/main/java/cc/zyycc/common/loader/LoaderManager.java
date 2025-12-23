package cc.zyycc.common.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoaderManager {

    public static final Map<String, ClassLoader> registerLoaders = new ConcurrentHashMap<>();


    public static ClassLoader registerClassLoader(MyLoader name, List<Path> jarPath, ClassLoader parent) {
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
            URLClassLoader loader = new URLClassLoader(urls, parent);
            registerClassLoader(name, loader);
            return loader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void registerClassLoader(MyLoader name, ClassLoader classLoader) {
        registerLoaders.put(name.getName(), classLoader);
    }

    public static ClassLoader getClassLoader(MyLoader loader) {
        return getClassLoader(loader.getName());
    }

    public static ClassLoader getClassLoader(String loaderName) {
        return registerLoaders.computeIfAbsent(loaderName,
                name -> ClassLoader.getSystemClassLoader()
        );

    }

}
