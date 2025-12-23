package cc.zyycc.plugin.tasks;


import cc.zyycc.plugin.ZyyPluginExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class DownloadInstallBK extends DefaultTask {

    private static final String url = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
  

    @Input
    public abstract Property<String> getMinecraftVersion();

    public File buildTools;
    @OutputFile
    public File getOutput() {
        return buildTools;
    }


    @TaskAction
    public void run() throws Exception {
        Path path = buildTools.toPath();

        buildTools.getParentFile().mkdirs();
        System.out.println("Downloading BuildTools");
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!path.getParent().resolve("spigot.jar").toFile().exists()) {
            System.out.println("install spigotmc");
            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", path.toString(), "--rev", getMinecraftVersion().get()
            );

            pb.directory(buildTools.getParentFile()); // 设置解压目录
            pb.inheritIO(); // 输出子进程日志
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("spigot build success");
            } else {
                System.err.println("error code：" + exitCode);
            }
        }


    }

}
