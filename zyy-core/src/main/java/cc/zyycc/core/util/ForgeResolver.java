package cc.zyycc.core.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeResolver {

    public static Path getForgeFormArgsPath(String mcVersion, String forgeVersion, String forgeLocalPath) {
        String sysType = File.pathSeparatorChar == ';' ? "win" : "unix";
        //1.19.2\libraries\net\minecraftforge\forge\1.19.2-43.3.8\win_args.txt
        return Paths.get(forgeLocalPath, "libraries", "net", "minecraftforge", "forge", mcVersion + "-" + forgeVersion, sysType + "_args.txt");
    }


    public static boolean forgeIsInstalled(String mcVersion, String forgeVersion, String forgeLocalPath) {
        boolean exists = Files.exists(getForgeFormArgsPath(mcVersion, forgeVersion, forgeLocalPath));
        if (!exists) {
            return false;
        }
        return true;
    }

    public static List<Supplier<Path>> checkMavenNoSource(Map<String, String> libraries, String forgeLocalPath) {
        List<Supplier<Path>> suppliers = new ArrayList<>();

        for (String coord : libraries.keySet()) {
            Path path = getLibraryPath(coord, forgeLocalPath);
            if (!Files.exists(path)) {
                suppliers.add(() -> path);
            }
        }

        return suppliers;
    }

    public static Path getLibraryPath(String mavenCoord, String forgeLocalPath) {
        // 示例: org.yaml:snakeyaml:1.33
        String[] parts = mavenCoord.split(":");
        if (parts.length < 3) throw new IllegalArgumentException("Invalid Maven coordinate: " + mavenCoord);

        String group = parts[0].replace('.', File.separatorChar); // org/yaml
        String artifact = parts[1]; // snakeyaml
        String version = parts[2]; // 1.33

        String fileName = artifact + "-" + version + ".jar";
        return Paths.get(forgeLocalPath, "libraries", group, artifact, version, fileName);
    }




}
