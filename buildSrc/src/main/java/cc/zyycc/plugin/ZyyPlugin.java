package cc.zyycc.plugin;


import cc.zyycc.plugin.tasks.BuildBK;
import cc.zyycc.plugin.tasks.DownloadBK;
import cc.zyycc.plugin.tasks.SetupSpigot;
import cc.zyycc.plugin.tasks.asm.AsmTransformTask;
import cc.zyycc.plugin.tasks.remap.RemapSpigot;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;


public class ZyyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        ZyyPluginExtension ext = project.getExtensions().create("zyyExtension", ZyyPluginExtension.class, project);

        TaskProvider<DownloadBK> downloadSpigot = project.getTasks().register("downloadSpigot", DownloadBK.class, task ->
                task.setOutputFile(ext.getBuildToolsFile())
        );

        project.getTasks().register("buildSpigot", BuildBK.class, task -> {
            task.dependsOn(downloadSpigot.get());
            task.getMinecraftVersion().set(ext.getMinecraftVersion());
            task.setBuildToolsFile(ext.getBuildToolsFile());
            task.setSpigotFile(ext.getSpigotFile());
        });


        RemapSpigot remapSpigot = new RemapSpigot(project, ext);
        project.getTasks().register("setupSpigot", SetupSpigot.class, task -> {
            task.setGroup("spigot");
            task.setDescription("Download BuildTools and build Spigot in one go");
            task.dependsOn(remapSpigot.getResultTask());
        });
//        project.getTasks().create("asmTransform", AsmTransformTask.class, task -> {
//            File input = new File(project.getBuildDir(), "libs/" + project.getName() + "-" + project.getVersion() + ".jar");
//            File output = new File(project.getBuildDir(), "libs/" + project.getName() + "-" + project.getVersion() + "-asm.jar");
//            task.setInputJar(input);
//            task.setOutputJar(output);
//            task.doLast(last -> {
//                setInputJar(trimmedJar)
//                setOutputJar(file("$buildDir/libs/zyy-spigot-1165.jar"));
//                addEnhancer(cc.zyycc.plugin.tasks.asm.TicketTypeAsm)
//            });
//        });


    }
}
