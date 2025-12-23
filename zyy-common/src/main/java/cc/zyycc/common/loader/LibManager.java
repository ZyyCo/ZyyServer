package cc.zyycc.common.loader;

import cc.zyycc.common.util.LoaderHandler;
import cc.zyycc.common.util.Version;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibManager {

    public final List<Path> libraries = new ArrayList<>();

    public LibManager() {
    }

    public LibManager(String[] path) {
        this.addLibrary(path);
    }


    public void addLibrary(String[] path) {
        for (String s : path) {
            addLibrary(Paths.get(s));
        }
    }

    public void addLibrary(List<Path> paths) {
        for (Path s : paths) {
            addLibrary(s);
        }
    }


    public void addLibrary(List<String> path, List<String> exclude) {
        for (String s : path) {
            boolean excludeLib = false;
            for (String s1 : exclude) {
                if (s.contains(s1)) {
                    System.out.println("已忽略:" + s);
                    excludeLib = true;
                }
            }
            if (!excludeLib) {
                addLibrary(Paths.get(s));
            }
        }

    }

    public void addLibrary(String path) {
        addLibrary(Paths.get(path));
    }

    public void addLibrary(String path, int priority) {
        addLibrary(Paths.get(path));
    }


    public void addLibrary(Path path) {
        libraries.add(path);
    }

    public void generateAppClassLoader() throws Exception {
        if (Version.greaterThanJava8()) {
            LoaderHandler.addSystemClassLoader(libraries);
        } else {
            LoaderHandler.addToPaths(ClassLoader.getSystemClassLoader(),
                    libraries);
        }


    }

}
