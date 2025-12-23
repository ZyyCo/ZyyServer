package cc.zyycc.plugin.tasks.remap;

import cc.zyycc.plugin.ZyyPluginExtension;
import cc.zyycc.plugin.util.SrgFile;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.transformer.MappingTransformer;
import net.minecraftforge.gradle.common.tasks.ExtractMCPData;
import net.minecraftforge.gradle.mcp.tasks.GenerateSRG;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.internal.impldep.org.objectweb.asm.commons.Remapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemapSpigot {

    private final ZyyPluginExtension ext;
    private final TaskProvider<Task> taskProvider;

    private final Project project;


    public RemapSpigot(Project project, ZyyPluginExtension ext) {
        this.project = project;
        this.ext = ext;
        RegularFileProperty remapFileLazy = ext.getRemapFile();

        TaskProvider<Task> copySpigot = copySpigot(remapFileLazy);

        this.taskProvider = new MappingsHandle(project, ext).task(remapFileLazy, copySpigot);

    }

    public TaskProvider<Task> copySpigot(RegularFileProperty remapFileLazy) {
        return project.getTasks().register("copySpigot", task -> {
            task.dependsOn(project.getTasksByName("buildSpigot", true));

            //从spigotFile拷贝到remapFileLazy
            task.doLast(copy -> {
                File spigotFile = ext.getSpigotFile();
                File remapFile = remapFileLazy.get().getAsFile();
                try {
                    Files.createDirectories(remapFile.toPath().getParent());
                    Files.copy(
                            spigotFile.toPath(),
                            remapFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    project.getLogger().lifecycle("Copied Spigot file from {} to {}", spigotFile, remapFile);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy Spigot file", e);
                }
            });
        });
    }


//    public TaskProvider<RenameJarInPlace> remapSpigot(RegularFileProperty remapFileLazy) {
//
//        return project.getTasks().register("remapSpigot", RenameJarInPlace.class, task -> {
//            task.dependsOn(renameSpigot);
//            task.getInput().set(remapFileLazy.get().getAsFile());
//            task.getMappings().set(tsrgLazy.get());
//            task.doLast(remap -> {
//                try {
//                    task.apply();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//        });
//    }


    public TaskProvider<Task> getResultTask() {
        return taskProvider;
    }


}


