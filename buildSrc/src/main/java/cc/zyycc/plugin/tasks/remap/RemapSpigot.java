package cc.zyycc.plugin.tasks;

import cc.zyycc.plugin.ZyyPluginExtension;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class RemapSpigot {

    TaskProvider<RenameSpigot> remapSpigot;

    public RemapSpigot(Project project, ZyyPluginExtension ext) {
        this.remapSpigot = project.getTasks().register("remapSpigot", RenameSpigot.class, task -> {
            task.dependsOn(project.getTasksByName("buildSpigot", true));
            task.setSpigotFile(ext.getSpigotFile());
            task.setRemapDir(ext.getRemapDir());
            task.getBukkitVersion().set(ext.getBukkitVersion());
        });
    }


    public TaskProvider<RenameSpigot> getTask() {
        return remapSpigot;
    }
}


