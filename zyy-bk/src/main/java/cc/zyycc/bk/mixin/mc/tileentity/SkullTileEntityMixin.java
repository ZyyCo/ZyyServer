package cc.zyycc.bk.mixin.mc.tileentity;

import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import cc.zyycc.bk.util.SkinCacheLoader;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.concurrent.*;

@Mixin(net.minecraft.tileentity.SkullTileEntity.class)
public abstract class SkullTileEntityMixin extends TileEntityMixin {
    private static final ExecutorService executor = Executors.newFixedThreadPool(3, (new ThreadFactoryBuilder()).setNameFormat("Head Conversion Thread - %1$d").build());

    private static final LoadingCache<String, GameProfile> skinCache = CacheBuilder.newBuilder().maximumSize(5000L).expireAfterAccess(60L, TimeUnit.MINUTES).build(new SkinCacheLoader());


    private static Future<GameProfile> b(final @Nullable GameProfile gameprofile, final Predicate<GameProfile> callback, boolean sync) {
        if (gameprofile != null && !StringUtils.isNullOrEmpty(gameprofile.getName())) {
            if (gameprofile.isComplete() && gameprofile.getProperties().containsKey("textures")) {
                callback.apply(gameprofile);
            } else {

                DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
                if (server != null) {
                    GameProfile profile = (GameProfile) skinCache.getIfPresent(gameprofile.getName().toLowerCase(Locale.ROOT));
                    if (profile != null && Iterables.getFirst(profile.getProperties().get("textures"), (Object) null) != null) {
                        callback.apply(profile);
                        return Futures.immediateFuture(profile);
                    }

                    Callable<GameProfile> callable = () -> {
                        final GameProfile profile1 = (GameProfile) skinCache.getUnchecked(gameprofile.getName().toLowerCase(Locale.ROOT));

                        ((MinecraftServerBridge) server).bridge$queuedProcess(new Runnable() {
                            public void run() {
                                if (profile1 == null) {
                                    callback.apply(gameprofile);
                                } else {
                                    callback.apply(profile1);
                                }

                            }
                        });
                        return profile1;
                    };
                    if (sync) {
                        try {
                            return Futures.immediateFuture((GameProfile) callable.call());
                        } catch (Exception ex) {
                            Throwables.throwIfUnchecked(ex);
                            throw new RuntimeException(ex);
                        }
                    }

                    return executor.submit(callable);
                }

                callback.apply(gameprofile);
            }
        } else {
            callback.apply(gameprofile);
        }

        return Futures.immediateFuture(gameprofile);
    }



}
