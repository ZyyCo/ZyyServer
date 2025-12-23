package cc.zyycc.plugin;


import cc.zyycc.plugin.tasks.DownloadInstallBK;
import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class ZyyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        ZyyPluginExtension ext = project.getExtensions().create("pluginExtension", ZyyPluginExtension.class, project);

        project.getTasks().register("downloadInstall", DownloadInstallBK.class, ext);

        project.getTasks().register("test", task -> {
            task.doLast(task1 -> {
                System.out.println("test" );
            });
        });

    }
}
