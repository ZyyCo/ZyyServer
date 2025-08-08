package cc.zyycc.forge.adapter;

import cc.zyycc.common.VersionInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ForgeAdapter {

    public List<String> buildCommand(Path argsPath) {
        List<String> modulePaths = new ArrayList<>();
        List<String> legacyClassPaths = new ArrayList<>();
        List<String> opens = new ArrayList<>();
        List<String> exports = new ArrayList<>();
        List<String> ignores = new ArrayList<>();
        try {
            for (String arg : Files.lines(argsPath).collect(Collectors.toList())) {
                if (arg.startsWith("-p ")) {
                    String modulePath = arg.substring(3).trim(); // 取 "-p " 后面的内容
//                    result.add("-p");
                    String[] paths = modulePath.split(File.pathSeparator);//分割;或:
                    modulePaths.addAll(Arrays.asList(paths));
                } else if (arg.startsWith("--add-opens ")) {
                    //result.add("--add-opens");
                    opens.add(arg.substring("--add-opens ".length()).trim());
                } else if (arg.startsWith("--add-exports ")) {
                    // result.add("--add-exports");
                    exports.add(arg.substring("--add-exports ".length()).trim());
                } else if (arg.startsWith("-D")) {
                    if (arg.startsWith("-DignoreList")) {
                        String ignoreLists = arg.substring("-DignoreList=".length()).trim();
                        ignores.addAll(Arrays.asList(ignoreLists.split(",")));
                    } else if (arg.startsWith("-DlegacyClassPath=")) {
                        String legacyClassPath = arg.substring("-DlegacyClassPath=".length()).trim();
                        String[] paths = legacyClassPath.split(File.pathSeparator);
                        legacyClassPaths.addAll(Arrays.asList(paths));
                    }
                }
            }



            List<String> commands = new ArrayList<>();
            commands.add("java");
            commands.add("-p");
            commands.add(String.join(File.pathSeparator, modulePaths));
            commands.add("--add-modules");
            commands.add("ALL-MODULE-PATH");
            for (String open : opens) {
                commands.add("--add-opens");
                commands.add(open);
            }
            for (String export : exports) {
                commands.add("--add-exports");
                commands.add(export);
            }
            commands.add("-Djava.net.preferIPv6Addresses=system");

            commands.add("-DignoreList=" + String.join(",", ignores));
            commands.add("-DlibraryDirectory=libraries");

            commands.add("-DlegacyClassPath=" + String.join(File.pathSeparator, legacyClassPaths));

            commands.add("cpw.mods.bootstraplauncher.BootstrapLauncher");
            commands.add("--launchTarget");
            commands.add("forgeserver");

            commands.add("--fml.forgeVersion");
            commands.add(VersionInfo.FORGE_VERSION);

            commands.add("--fml.mcVersion");
            commands.add(VersionInfo.MINECRAFT_VERSION);


            commands.add("--fml.forgeGroup");
            commands.add("net.minecraftforge");

            commands.add("--fml.mcpVersion");
            commands.add(VersionInfo.MCP_VERSION);

            return commands;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
