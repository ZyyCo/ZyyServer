package cc.zyycc.plugin.tasks;

import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.internal.impldep.org.objectweb.asm.commons.Remapper;


import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public abstract class RenameSpigot extends DefaultTask {

    private File spigotFile;

    private File remapDir;

    private final File remapFile;
    @Input
    public abstract Property<String> getBukkitVersion();



    @Inject
    public RenameSpigot() {
        this.remapFile = new File(remapDir, "remapSpigot.jar");
    }

    @TaskAction
    public void renameSpigot() throws IOException {

        if (!remapDir.exists()) {
            Files.createDirectory(remapDir.toPath());
        }


        File remapFile = new File(remapDir, "remapSpigot.jar");
        if (!remapFile.exists()) {
            Files.copy(spigotFile.toPath(), remapFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        File targetFile = new File(remapDir, "craftbukkit.tsrg");

        try (InputStream in = RenameSpigot.class.getClassLoader().getResourceAsStream("craftbukkit.tsrg")) {
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        RenameJarInPlace renameTask = getProject().getTasks()
                .create("renameSpigotJarTmp", RenameJarInPlace.class);
        renameTask.getInput().set(remapFile);
        renameTask.getMappings().set(targetFile);
        renameTask.apply();


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

    public void setRemapDir(File remapDir) {
        this.remapDir = remapDir;
    }

    @OutputFile
    public File getRemapFile() {
        return remapFile;
    }
}
