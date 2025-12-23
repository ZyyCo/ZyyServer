package cc.zyycc.forge.mod;

import cc.zyycc.common.VersionInfo;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.stream.Stream;

public class MyMinecraftLocator extends AbstractJarFileLocator {
    Path mcPath;
    FileSystem fileSystem = null;
    public MyMinecraftLocator() {
        mcPath = FMLLoader.getMCPaths()[0];
        try {
            fileSystem = FileSystems.newFileSystem(this.mcPath, getClass().getClassLoader());
            System.out.println("fsdfhudhfiudfd1." + fileSystem);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public List<IModFile> scanMods() {
        ModFile modFile = ModFile.newFMLInstance(mcPath, this);
        return Collections.singletonList(modFile);
    }

    @Override
    public String name() {
        return "minecraft";
    }



    @Override
    public Path findPath(IModFile modFile, String... path) {
        if (path.length == 2 && "META-INF".equals(path[0]) && "mods.toml".equals(path[1])) {
            try {
                URI uri = getClass().getClassLoader().getResource("minecraftmod.toml").toURI();
                if ("jar".equals(uri.getScheme())) {
                    System.out.println("djskldhkjshdkjshduikshdiuhwsuidhsuihduis: " + path[1]);
                    FileSystems.newFileSystem(uri, new HashMap<>());
                }
                return Paths.get(uri);
            } catch (Exception e) {
                throw new RuntimeException("低调低调", e);
            }
        }
        return mcPath.resolve(Paths.get(path[0], Arrays.copyOfRange(path, 1, path.length)));
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {
        System.out.println("dsdsidjsijdoisjdos扫描文件");
        Path path;
        if (Files.isDirectory(this.mcPath, new java.nio.file.LinkOption[0])) {
            path = this.mcPath;
        } else {
            path = this.fileSystem.getPath("/", new String[0]);
        }
        try (Stream<Path> files = Files.find(path, 2147483647, (p, a) -> (p.getNameCount() > 0 && p.getFileName().toString().endsWith(".class")), new java.nio.file.FileVisitOption[0])) {
            files.forEach(pathConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Manifest> findManifest(Path file) {
        return Optional.empty();
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }

    @Override
    public boolean isValid(IModFile modFile) {
        return true;
    }


}
