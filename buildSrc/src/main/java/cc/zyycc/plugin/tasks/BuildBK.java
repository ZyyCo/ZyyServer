package cc.zyycc.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;

public abstract class BuildBK extends DefaultTask {

    private File buildToolsFile;

    public File spigotFile;
    @Input
    public abstract Property<String> getMinecraftVersion();

    @TaskAction
    public void run() {
        if (spigotFile.exists()) {
            return;
        }
        getProject().exec(spec -> {
            spec.workingDir(buildToolsFile.getParent());
            try {
                System.out.println("install spigotmc");
                spec.commandLine("java", "-jar", buildToolsFile.getCanonicalFile(), "--rev", getMinecraftVersion().get());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setSpigotFile(File spigotFile) {
        this.spigotFile = spigotFile;
    }

    @OutputFile
    public File getSpigotFile() {
        return spigotFile;
    }

    public void setBuildToolsFile(File buildToolsFile) {
        this.buildToolsFile = buildToolsFile;
    }

    @Internal
    public File getBuildToolsFile() {
        return buildToolsFile;
    }
}
