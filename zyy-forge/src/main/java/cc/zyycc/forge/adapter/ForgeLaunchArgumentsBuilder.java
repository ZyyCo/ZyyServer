package cc.zyycc.forge.adapter;

import cc.zyycc.common.VersionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeLaunchArgumentsBuilder {


    public static void build(List<String> args) {
        args.add("--launchTarget");
        args.add("forgeserver");

        args.add("--fml.forgeVersion");
        args.add(VersionInfo.FORGE_VERSION);

        args.add("--fml.mcVersion");
        args.add(VersionInfo.MINECRAFT_VERSION);

        args.add("--fml.forgeGroup");
        args.add("net.minecraftforge");

        args.add("--fml.mcpVersion");

    }

    /**
     * 添加通用 JVM 参数（1.19.2 ~ 1.20.1 版本通用）
     */
    public static void applyJvmArgs(Consumer<String> add) {
        add.accept("--add-modules");
        add.accept("ALL-MODULE-PATH");

        add.accept("--add-opens");
        add.accept("java.base/java.util.jar=cpw.mods.securejarhandler");
        add.accept("--add-opens");
        add.accept("java.base/java.lang.invoke=cpw.mods.securejarhandler");

        add.accept("--add-exports");
        add.accept("java.base/sun.security.util=cpw.mods.securejarhandler");
        add.accept("--add-exports");
        add.accept("jdk.naming.dns/com.sun.jndi.dns=java.naming");

        add.accept("-Djava.net.preferIPv6Addresses=system");
        add.accept("-DlibraryDirectory=libraries");
    }

    /**
     * 添加 Forge 启动参数（除了 mcpVersion，其他都一样）
     */
    public static void applyForgeArgs(Consumer<String> add, String forgeVersion, String mcVersion) {
        add.accept("--launchTarget");
        add.accept("forgeserver");

        add.accept("--fml.forgeVersion");
        add.accept(forgeVersion);

        add.accept("--fml.mcVersion");
        add.accept(mcVersion);

        add.accept("--fml.forgeGroup");
        add.accept("net.minecraftforge");
    }
}
