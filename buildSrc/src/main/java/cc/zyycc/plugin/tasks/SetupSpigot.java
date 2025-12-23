package cc.zyycc.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFile;

import java.io.File;

public class SetupSpigot extends DefaultTask {

    private File outputFile;

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    @OutputFile
    public File getOutputFile() {
        return outputFile;
    }
}
