package cc.zyycc.bk.mixin.core.server;

import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Main.class)
public class MainMixin {


//    @Redirect(method = "lambda$main$3", at = @At(value = "NEW", target = "(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/resources/DataPackRegistries;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/server/ServerPropertiesProvider;Lcom/mojang/datafixers/DataFixer;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/management/PlayerProfileCache;Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;)Lnet/minecraft/server/dedicated/DedicatedServer;"))
//    @SuppressWarnings("target")
//    private static DedicatedServer main(Thread thread, DynamicRegistries.Impl p_i232601_2_, SaveFormat.LevelSave p_i232601_3_, ResourcePackList p_i232601_4_, DataPackRegistries p_i232601_5_, IServerConfiguration iServerConfiguration, ServerPropertiesProvider p_i232601_7_, DataFixer dataFixer, MinecraftSessionService p_i232601_9_, GameProfileRepository p_i232601_10_, PlayerProfileCache playerProfileCache, IChunkStatusListenerFactory p_i232601_12_) {
//        return new CDedicatedServer(thread, p_i232601_2_, p_i232601_3_, p_i232601_4_, p_i232601_5_, iServerConfiguration, p_i232601_7_, dataFixer, p_i232601_9_, p_i232601_10_, playerProfileCache, p_i232601_12_);
//    }
}
