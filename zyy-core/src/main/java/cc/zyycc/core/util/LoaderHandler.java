package cc.zyycc.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

public class LoaderHandler {
    public static void addToPaths(ClassLoader loader, List<Path> paths) throws Exception {

        Field ucpField;
        try {
            ucpField = loader.getClass().getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
        }
        ucpField.setAccessible(true);
        Object ucp = ucpField.get(loader);
        Method addUrlMethod = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);


        for (Path path : paths) {
            addUrlMethod.invoke(ucp, path.toUri().toURL());
        }
    }

    public static void addToPath(ClassLoader loader, Path path) throws Exception {

        Field ucpField;
        try {
            ucpField = loader.getClass().getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            ucpField = loader.getClass().getSuperclass().getDeclaredField("ucp");
        }
        ucpField.setAccessible(true);
        Object ucp = ucpField.get(loader);
        Method addUrlMethod = ucp.getClass().getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);

        addUrlMethod.invoke(ucp, path.toUri().toURL());

    }


}
