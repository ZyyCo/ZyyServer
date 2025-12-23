package cc.zyycc.common.bridge;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;

import static java.util.Arrays.asList;

public class BridgeHolder {

    public static OptionSet options;

    public static void setOptionParser(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("bukkit-settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("bukkit.yml"))
                .describedAs("File for bukkit settings");
        parser.accepts( "commands-settings", "File for command settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("commands.yml"))
                .describedAs("Yml file");
        parser.accepts( "plugins", "plugins dir")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("plugins"))
                .describedAs("Plugin dir");

        parser.accepts("world", "World name")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("World name");



        parser.accepts("spigot-settings", "File for spigot settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("spigot.yml"))
                .describedAs("Yml file");


        options = parser.parse(args);
    }



}
