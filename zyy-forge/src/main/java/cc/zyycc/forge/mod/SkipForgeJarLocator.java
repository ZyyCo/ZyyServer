package cc.zyycc.forge.mod;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.Manifest;

public class SkipForgeJarLocator extends AbstractJarFileLocator {
    @Override
    public List<IModFile> scanMods() {
        System.out.println("🔥 SkipForgeJarLocator.scanMods() 被调用");
        return Collections.emptyList(); // 不返回任何 mod
    }
    @Override
    public String name() {
        return "SkipForgeJarLocator";
    }



    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {

    }

    @Override
    public Optional<Manifest> findManifest(Path file) {
        return Optional.empty();
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        System.out.println("🛠 SkipForgeJarLocator.initArguments 被调用");
    }


}
