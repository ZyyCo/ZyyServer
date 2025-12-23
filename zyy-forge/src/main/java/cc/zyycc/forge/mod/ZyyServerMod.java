package cc.zyycc.forge.mod;


import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.util.FileManager;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ZyyServerMod extends AbstractJarFileLocator {

    private final IModFile modFile;

    public ZyyServerMod() {
        // modPath = Paths.get(MainForge.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        Path cacheJar = FileManager.getCacheJar("bkao.jar");
        this.modFile = ModFile.newFMLInstance(cacheJar, this);
        this.modJars.put(modFile, createFileSystem(modFile));

    }

    @Override
    public List<IModFile> scanMods() {
        return Collections.singletonList(modFile);
    }

    @Override
    public Path findPath(IModFile modFile, String... path) {
        FileSystem fs = modJars.get(modFile);

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
