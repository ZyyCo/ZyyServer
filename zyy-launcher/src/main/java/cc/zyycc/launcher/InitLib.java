package cc.zyycc.launcher;

import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.bridge.InstrumentationBridge;
import cc.zyycc.common.install.InstallForge;
import cc.zyycc.common.loader.LibManager;
import cc.zyycc.common.loader.LoaderManager;
import cc.zyycc.common.loader.MyLoader;
import cc.zyycc.common.util.DownloaderLib;
import cc.zyycc.common.util.FileManager;
import cc.zyycc.common.util.ServerUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;


public class InitLib {

    public static void startForgeServer(String[] args, ClassLoader classLoader, boolean canStart) throws Exception {

        Class<?> cl = classLoader.loadClass("cc.zyycc.forge.MainForge");
        cl.getMethod("startForgeServer", String[].class, boolean.class)
                .invoke(null, (Object) args, canStart);
    }

    public static ClassLoader initLoader() throws Exception {

        List<Path> classPath = new ArrayList<>();

        List<Path> extraLibraries = DownloaderLib.downloadExtraMavenJar(VersionInfo.EXTRA_LIBS);

        Path forgePath = Paths.get(VersionInfo.WORKING_DIR, "forge-" + VersionInfo.FORGE_FULL_VERSION + ".jar");
        //安装
        String[] libs = installForge(forgePath);

        LibManager libManager = new LibManager(libs);
        classPath.add(FileManager.getCacheJar("zyyaruzi.jar"));
        classPath.add(FileManager.getCacheJar("nashicore.jar"));
        //  classPath.add(new File("libraries/com/google/guava/guava/25.1-jre/guava-25.1-jre.jar").toPath());
        classPath.add(forgePath);

        libManager.addLibrary(extraLibraries);

        libManager.generateAppClassLoader();

        return LoaderManager.registerClassLoader(MyLoader.ZYY, classPath, ClassLoader.getSystemClassLoader());
    }

    public static String[] installForge(Path forgeJar) throws IOException, InterruptedException {
        String forgeInstallerUrl = "https://maven.minecraftforge.net/net/minecraftforge/forge/" + VersionInfo.FORGE_FULL_VERSION + "/forge-" + VersionInfo.FORGE_FULL_VERSION + "-installer.jar";

        return InstallForge.checkOrInstall(forgeInstallerUrl, VersionInfo.INSTALLER_FILE,
                VersionInfo.WORKING_DIR, forgeJar,
                () -> ServerUtil.loadClassPath(forgeJar));
    }


}
