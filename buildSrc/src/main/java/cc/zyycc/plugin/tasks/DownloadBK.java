package cc.zyycc.plugin.tasks;


import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class DownloadBK extends DefaultTask {

    private static final String url = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";

    private File buildToolsFile;
    @OutputFile
    public File getOutputFile() {
        return buildToolsFile;
    }

    public void setOutputFile(File file) {
        this.buildToolsFile = file;
    }


    @TaskAction
    public void run() {
        if (buildToolsFile.exists()) {
            return;
        }
        buildToolsFile.mkdirs();
        getLogger().lifecycle("Downloading BuildTools jar...");

//        new URL(url).withInputStream { i ->
//                buildTools.withOutputStream { o -> o << i }
//        }


        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, buildToolsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            getLogger().lifecycle("下载失败！");
            e.printStackTrace();
        }
    }

}
