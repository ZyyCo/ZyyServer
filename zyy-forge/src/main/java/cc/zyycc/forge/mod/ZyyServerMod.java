package cc.zyycc.bk.mod;


import cc.zyycc.bk.BKMain;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ZyyServerMod extends AbstractJarFileLocator {

    private final IModFile modFile;
    private final  Path modPath;

    public ZyyServerMod() {
        try {
             modPath = Paths.get(BKMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            FileSystem fileSystem = FileSystems.newFileSystem(modPath, BKMain.class.getClassLoader());
            this.modFile = ModFile.newFMLInstance(modPath, this);
            this.modJars.put(modFile, fileSystem);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IModFile> scanMods() {
        return Collections.singletonList(modFile);
    }

    @Override
    public Path findPath(IModFile modFile, String... path) {
        FileSystem fs = modJars.get(modFile);
        if (fs == null) throw new IllegalStateException("草No FileSystem for modFile");
        return fs.getPath("", path);
    }

    @Override
    public String name() {
        return "zyyserver";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {

    }

}
