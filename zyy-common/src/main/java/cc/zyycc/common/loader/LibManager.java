package cc.zyycc.common.bridge.loader;

import cc.zyycc.common.util.LoaderHandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LibManager {

    public static final List<Path> libraries = new ArrayList<>();


    public static void addLibrary(String[] path) {
        for (String s : path) {
            addLibrary(Paths.get(s));
        }
    }

    public static void addLibrary(List<Path> paths) {
        for (Path s : paths) {
            addLibrary(s);
        }
    }


    public static void addLibrary(List<String> path, List<String> exclude) {
        for (String s : path) {
            boolean excludeLib = false;
            for (String s1 : exclude) {
                if (s.contains(s1)) {
                    System.out.println("已忽略:" + s);
                    excludeLib = true;
                }
            }
            if(!excludeLib){
                addLibrary(Paths.get(s));
            }
        }

    }

    public static void addLibrary(String path) {
        addLibrary(Paths.get(path));
    }

    public static void addLibrary(String path, int priority) {
        addLibrary(Paths.get(path));
    }


    public static void addLibrary(Path path) {
        libraries.add(path);
    }

    public static void generateAppClassLoader() throws Exception {
        LoaderHandler.addToPaths(ClassLoader.getSystemClassLoader(), LibManager.libraries);
    }

}
