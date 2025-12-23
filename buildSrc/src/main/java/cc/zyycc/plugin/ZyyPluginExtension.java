package cc.zyycc.plugin;


import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;


import javax.inject.Inject;
import java.io.File;
import java.util.function.Supplier;

public abstract class ZyyPluginExtension {

    public abstract Property<String> getMinecraftVersion();

    public abstract Property<String> getBukkitVersion();

    public abstract RegularFileProperty getRemapFile();

    private final File buildToolsDir;
    private final File buildToolsFile;
    private final Supplier<File> spigotFile;


    @Inject
    public ZyyPluginExtension(Project project) {
        this.buildToolsDir = new File(project.getBuildDir(), "cache/buildTools");
        this.spigotFile = () -> new File(buildToolsDir, "spigot-" + getMinecraftVersion().get() + ".jar");
        this.buildToolsFile = new File(buildToolsDir, "BuildTools.jar");

        //getRemapFile().convention(() -> new File(project.getBuildDir(), "cache/remap/spigot--remap.jar"));


    }


    public File getSpigotFile() {
        return spigotFile.get();
    }


    public File getBuildToolsFile() {
        return buildToolsFile;
    }

    public File getBuildToolsDir() {
        return buildToolsDir;
    }
}
