package cc.zyycc.bk.bridge;

import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.spigotmc.SpigotWorldConfig;

public interface WorldBridge {

    CraftWorld bridge$getWorld();

    CraftServer bridge$getCraftServer();

    void bridge$setPopulating(boolean populating);


    String bridge$getWorldName();

    SpigotWorldConfig bridge$spigotConfig();

    void bridge$setGenerator(org.bukkit.generator.ChunkGenerator generator);
    void bridge$setEnvironment(org.bukkit.World.Environment environment);



    org.bukkit.generator.ChunkGenerator bridge$getGenerator();
}
