package cc.zyycc.plugin.tasks.remap;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class RenameSpigot extends DefaultTask {

    private File spigotFile;
    private File remapFile;


    @Input
    public abstract Property<String> getBukkitVersion();


    @TaskAction
    public void renameSpigot() throws IOException {
        File remapDir = remapFile.getParentFile();
        if (!remapDir.exists()) {
            Files.createDirectory(remapDir.toPath());
        }
        if (!remapFile.exists()) {
            Files.copy(spigotFile.toPath(), remapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }


//        Remapper remapper = new Remapper() {
//            @Override
//            public String map(String internalName) {
//                String bkVersion = getBukkitVersion().get();
//                if (internalName.equals("net/minecraft/server/MinecraftServer")) {
//                    return "net/minecraft/server/" + bkVersion + "/MinecraftServer";
//                }
//                return super.map(internalName);
//            }
//        };


//        getProject().afterEvaluate(p -> {
//            Task renameTask = p.getTasks().getByName("renameSpigot");
//            renameTask.doLast(t -> {
//                // 这里可以通过反射去调用 extraMapping 方法
//                try {
//                    Method extraMapping = renameTask.getClass().getMethod("extraMapping", File.class);
//                    extraMapping.invoke(renameTask, new File("extra.srg"));
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        });


    }

    public void setSpigotFile(File spigotFile) {
        this.spigotFile = spigotFile;
    }



    public void setRemapFile(File remapFile) {
        this.remapFile = remapFile;
    }
}
