package cc.zyycc.bk.bridge.server;

import cc.zyycc.bk.bridge.WorldBridge;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Map;
import java.util.UUID;

public interface ServerWorldBridge extends WorldBridge {
    IServerWorldInfo zyyServer$getWorldInfo();

    MinecraftServer zyyServer$getServer();

    ServerWorld zyyServer$getServerWorld();


    boolean bridge$addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason);

    CreatureSpawnEvent.SpawnReason bridge$getSpawnReason();


    UUID uuid();

    Map<UUID, Entity> bridge$getEntitiesByUuid();

    Int2ObjectMap<Entity> bridge$getEntitiesById();


    ServerWorldInfo bridge$getServerWorldInfo();



}
