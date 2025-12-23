package cc.zyycc.plugin.tasks

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class BuildSpigot {

    public File buildToolsFile;

    public Provider<File> spigotFile;

    @Input
    abstract Property<String> getMinecraftVersion();

    @TaskAction
    void run() throws Exception {
        if (spigotFile.get().exists()) {
            return;
        }
        println "Building Spigot ${minecraftVersion}..."
        exec {
            workingDir libDir
            commandLine "java", "-jar", "BuildTools.jar", "--rev", minecraftVersion
        }
    }

    void setSpigotFile(Provider<File> spigotFile) {
        this.spigotFile = spigotFile;
    }
    @OutputFile
    Provider<File> getSpigotFile() {
        return spigotFile;
    }

    void setBuildToolsFile(File buildToolsFile) {
        this.buildToolsFile = buildToolsFile;
    }

    @InputFile
    File getBuildToolsFile() {
        return buildToolsFile;
    }

}
