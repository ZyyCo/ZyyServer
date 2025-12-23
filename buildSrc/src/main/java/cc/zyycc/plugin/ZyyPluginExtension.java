package cc.zyycc.plugin;


import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;

public abstract class ZyyPluginExtension {
    public abstract RegularFileProperty getBuildTools();
    public String minecraftVersion;

    public ZyyPluginExtension(Project project) {
        getBuildTools().convention(
               project.getLayout().getBuildDirectory().file("cache/BuildTools.jar"));
    }


}
