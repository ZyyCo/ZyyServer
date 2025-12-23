package cc.zyycc.plugin.util;

import org.gradle.api.Task;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Provider;
import org.gradle.api.file.RegularFileProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SrgFile {
    public static Map<String, String> flatten(File file) {
        Map<String, String> mapping = new HashMap<>();
        Map<MethodEntry, String> entry = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(file.toPath());) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.equals("net/minecraft/nbt/ListNBT net/minecraft/nbt/ListNBT")){
                    System.out.println( line);
                }
                if (Character.isWhitespace(line.charAt(0))) {

                    line = line.trim();
                    String[] split = line.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < split.length - 1; i++) {
                        sb.append(split[i]);
                        if (i != split.length - 2) {
                            sb.append(" ");
                        }
                    }
                    mapping.put(split[split.length - 1], String.valueOf(sb));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mapping;
    }

}
