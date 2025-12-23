package cc.zyycc.bk.mixin;

import cc.zyycc.bk.bridge.WorldBridge;
import cc.zyycc.bk.bridge.network.NetworkManagerBridge;
import cc.zyycc.bk.bridge.network.login.ServerLoginNetHandlerBridge;
import cc.zyycc.bk.bridge.network.play.ServerPlayNetHandlerBridge;
import cc.zyycc.bk.bridge.player.LivingEntityBridge;
import cc.zyycc.bk.bridge.player.ServerPlayerEntityBridge;
import cc.zyycc.bk.bridge.server.SimpleBridge;
import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import cc.zyycc.bk.bridge.server.management.PlayerListBridge;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeBookStatus;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.*;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;


import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.v1_16_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.SpigotConfig;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;


@Mixin(net.minecraft.server.management.PlayerList.class)

public abstract class PlayerListMixin implements PlayerListBridge {
    @Final
    @Shadow
    public List<ServerPlayerEntity> players;
    @Shadow
    @Final
    private MinecraftServer server;

    private CraftServer craftServer;
    @Shadow
    @Final
    private BanList bannedPlayers;

    @Shadow
    @Final
    private Map<UUID, ServerPlayerEntity> uuidToPlayerMap;

    @Shadow
    public abstract void sendWorldInfo(ServerPlayerEntity playerIn, ServerWorld worldIn);

    @Shadow
    public abstract void updatePermissionLevel(ServerPlayerEntity player);

    @Shadow
    public abstract void sendInventory(ServerPlayerEntity playerIn);

    @Shadow
    protected abstract void writePlayerData(ServerPlayerEntity playerIn);

    @Shadow
    public abstract CompoundNBT readPlayerDataFromFile(ServerPlayerEntity playerIn);

    @Shadow
    protected abstract void setPlayerGameTypeBasedOnOther(ServerPlayerEntity target, @Nullable ServerPlayerEntity source, ServerWorld worldIn);

    @Shadow
    @Final
    private static SimpleDateFormat DATE_FORMAT;

    @Shadow
    protected abstract void sendScoreboard(ServerScoreboard scoreboardIn, ServerPlayerEntity playerIn);

    @Shadow
    public abstract void func_232641_a_(ITextComponent p_232641_1_, ChatType p_232641_2_, UUID p_232641_3_);

    @Shadow
    public abstract void sendPacketToAllPlayers(IPacket<?> packetIn);

    @Shadow
    public abstract boolean canJoin(GameProfile profile);

    @Shadow
    @Final
    private DynamicRegistries.Impl field_232639_s_;
    @Shadow
    @Final
    protected int maxPlayers;
    @Shadow
    private int viewDistance;

    @Shadow
    public abstract BanList getBannedPlayers();

    @Shadow
    public abstract IPBanList getBannedIPs();

    @Shadow
    public abstract boolean bypassesPlayerLimit(GameProfile profile);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void PlayerListMixinCode(MinecraftServer minecraftServer, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_, CallbackInfo ci) {


        for(Enchantment enchantment :  ForgeRegistries.ENCHANTMENTS) {
            org.bukkit.enchantments.Enchantment.registerEnchantment(new CraftEnchantment(enchantment));
        }
        for(Effect effect : ForgeRegistries.POTIONS) {
            PotionEffectType.registerPotionEffectType(new CraftPotionEffectType(effect));
        }

        SimpleBridge.craftServer = craftServer = new CraftServer((DedicatedServer) minecraftServer, (PlayerList) (Object) this);
        ((MinecraftServerBridge) minecraftServer).bridge$setConsole(ColouredConsoleSender.getInstance());

    }

    //(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/world/server/ServerWorld;ZLorg/bukkit/Location;Z)Lnet/minecraft/entity/player/ServerPlayerEntity;

    public ServerPlayerEntity moveToWorld(ServerPlayerEntity player, ServerWorld serverWorld, boolean flag, Location location, boolean avoidSuffocation) {

        player.stopRiding();
        this.players.remove(player);
        //this.playersByName.remove(player.getName().toLowerCase(Locale.ROOT));
        player.getServerWorld().removePlayer(player, true);
        player.revive();


        BlockPos blockPos = player.func_241140_K_();
        float f = player.func_242109_L();

        boolean flag1 = player.func_241142_M_();
        ServerPlayerEntity entityplayer1 = player;//????????????
        org.bukkit.World fromWorld = ((ServerPlayerEntityBridge) player).getBukkitEntity().getWorld();
        player.queuedEndExit = false;
        //   player.connection = player.connection;

//
//        player.copyFrom(player, flag);
//        player.setEntityId(player.getEntityId());
//        player.setPrimaryHand(player.getPrimaryHand());
//
//        for (String s : player.getTags()) {
//            player.addTag(s);
//        }

        boolean flag2 = false;
        if (location == null) {
            boolean isBedSpawn = false;
            ServerWorld serverWorld1 = this.server.getWorld(player.func_241141_L_());
            if (serverWorld1 != null) {
                Optional optional;
                if (blockPos != null) {
                    //getBed
                    optional = PlayerEntity.func_242374_a(serverWorld1, blockPos, f, flag1, flag);
                } else {
                    optional = Optional.empty();
                }

                if (!optional.isPresent()) {
                    if (blockPos != null) {
                        entityplayer1.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.SPAWN_NOT_VALID, 0.0F));
                    }
                } else {
                    BlockState iblockdata = serverWorld1.getBlockState(blockPos);
                    boolean flag3 = iblockdata.matchesBlock(Blocks.RESPAWN_ANCHOR);
                    Vector3d vec3d = (Vector3d) optional.get();
                    float f1;

                    if (!iblockdata.isIn(BlockTags.BEDS) && !flag3) {
                        f1 = f;
                    } else {
                        Vector3d vec3d1 = Vector3d.copyCenteredHorizontally(blockPos).subtract(vec3d).normalize();
                        f1 = (float) MathHelper.wrapDegrees(MathHelper.atan2(vec3d1.z, vec3d1.x) * (double) (180F / (float) Math.PI) - (double) 90.0F);
                    }

                    entityplayer1.func_242111_a(serverWorld1.getDimensionKey(), blockPos, f, flag1, false);
                    flag2 = !flag && flag3;
                    isBedSpawn = true;
                    location = new Location(((WorldBridge) serverWorld1).bridge$getWorld(), vec3d.x, vec3d.y, vec3d.z, f1, 0.0F);
                }
            }

            if (location == null) {
                serverWorld1 = this.server.getWorld(World.OVERWORLD);
                blockPos = ((ServerPlayerEntityBridge) player).bridge$getSpawnPoint(serverWorld1);
                location = new Location(((WorldBridge) serverWorld1).bridge$getWorld(), (double) ((float) blockPos.getX() + 0.5F),
                        (double) ((float) blockPos.getY() + 0.1F), (double) ((float) blockPos.getZ() + 0.5F));
            }

            Player respawnPlayer = SimpleBridge.craftServer.getPlayer(entityplayer1);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn && !flag2, flag2);
            SimpleBridge.craftServer.getPluginManager().callEvent(respawnEvent);
            if (((ServerPlayNetHandlerBridge) player.connection).bridge$isDisconnected()) {
                return player;
            }

            location = respawnEvent.getRespawnLocation();
            if (!flag) {
                ((ServerPlayerEntityBridge) player).bridge$reset();
            }
        } else {
            location.setWorld(((WorldBridge) serverWorld).bridge$getWorld());
        }

        ServerWorld worldserver1 = ((CraftWorld) location.getWorld()).getHandle();
        entityplayer1.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        player.connection.captureCurrentPosition();//111
        while (avoidSuffocation && !worldserver1.hasNoCollisions(entityplayer1) && entityplayer1.getPosY() < (double) 256.0F) {
            entityplayer1.setPosition(entityplayer1.getPosX(), entityplayer1.getPosY() + (double) 1.0F, entityplayer1.getPosZ());
        }

        IWorldInfo worlddata = worldserver1.getWorldInfo();
        entityplayer1.connection.sendPacket(new SRespawnPacket(worldserver1.getDimensionType(),
                worldserver1.getDimensionKey(), BiomeManager.getHashedSeed(worldserver1.getSeed()),
                entityplayer1.interactionManager.getGameType(), entityplayer1.interactionManager.func_241815_c_(),
                worldserver1.isDebug(), worldserver1.isFlatWorld(), flag));
        entityplayer1.connection.sendPacket(new SUpdateViewDistancePacket(((WorldBridge) serverWorld).bridge$spigotConfig().viewDistance));
        entityplayer1.setWorld(worldserver1);
        ((LivingEntityBridge) entityplayer1).bridge$setDead(false);


        ((ServerPlayNetHandlerBridge) entityplayer1.connection).bridge$teleport
                (new Location(((WorldBridge) serverWorld).bridge$getWorld(), entityplayer1.getPosX(), entityplayer1.getPosY(), entityplayer1.getPosZ(), entityplayer1.rotationYaw, entityplayer1.rotationPitch));
        entityplayer1.setSneaking(false);
        entityplayer1.connection.sendPacket(new SWorldSpawnChangedPacket(worldserver1.getSpawnPoint(), worldserver1.getSpawnAngle()));
        entityplayer1.connection.sendPacket(new SServerDifficultyPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        entityplayer1.connection.sendPacket(new SSetExperiencePacket(entityplayer1.experience, entityplayer1.experienceTotal, entityplayer1.experienceLevel));
        this.sendWorldInfo(entityplayer1, worldserver1);
        this.updatePermissionLevel(entityplayer1);
        if (!((ServerPlayNetHandlerBridge) player.connection).bridge$isDisconnected()) {
            worldserver1.addRespawnedPlayer(entityplayer1);
            this.players.add(entityplayer1);
//            this.playersByName.put(entityplayer1.getName().toLowerCase(Locale.ROOT), entityplayer1);
            this.uuidToPlayerMap.put(entityplayer1.getUniqueID(), entityplayer1);
        }

        entityplayer1.setHealth(entityplayer1.getHealth());
        if (flag2) {
            entityplayer1.connection
                    .sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE,
                            SoundCategory.BLOCKS, (double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ(), 1.0F, 1.0F));
        }

        // this.sendInventory(player);
        this.updateClient(player);
        player.sendPlayerAbilities();

        for (Object o1 : player.getActivePotionEffects()) {
            EffectInstance mobEffect = (EffectInstance) o1;
            player.connection.sendPacket(new SPlayEntityEffectPacket(player.getEntityId(), mobEffect));
        }

        player.func_213846_b(((CraftWorld) fromWorld).getHandle());
        if (fromWorld != location.getWorld()) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(((ServerPlayerEntityBridge) player).getBukkitEntity(), fromWorld);
            ((cc.zyycc.bk.bridge.SimpleBridge) this.server).bridge$getCraftServer().getPluginManager().callEvent(event);
        }

        if (((ServerPlayNetHandlerBridge) player.connection).bridge$isDisconnected()) {
            this.writePlayerData(player);
        }

        return entityplayer1;

    }


    @Override
    public ServerPlayerEntity bridge$canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile, ServerLoginNetHandler handler) {
        UUID uuid = PlayerEntity.getUUID(gameProfile);
        List<ServerPlayerEntity> list = Lists.newArrayList();
        for (ServerPlayerEntity entityplayer : this.players) {
            if (entityplayer.getUniqueID().equals(uuid)) {
                list.add(entityplayer);
            }
        }
        for (ServerPlayerEntity entityplayer : list) {
            this.writePlayerData(entityplayer);
            entityplayer.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
        }
        ServerPlayerEntity entity = new ServerPlayerEntity(this.server, this.server.getWorld(World.OVERWORLD), gameProfile, new PlayerInteractionManager(this.server.getWorld(World.OVERWORLD)));
        Player player = ((ServerPlayerEntityBridge) entity).getBukkitEntity();

        String hostname = handler == null ? "" : ((ServerLoginNetHandlerBridge) handler).bridge$getHostname();
        InetAddress realAddress = handler == null ? ((InetSocketAddress) socketAddress).getAddress() : ((InetSocketAddress) ((NetworkManagerBridge) handler.networkManager).bridge$getRawAddress()).getAddress();

        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, ((InetSocketAddress) socketAddress).getAddress(), realAddress);
        if (this.getBannedPlayers().isBanned(gameProfile) && !this.getBannedPlayers().getEntry(gameProfile).hasBanExpired()) {
            ProfileBanEntry gameprofilebanentry = this.bannedPlayers.getEntry(gameProfile);
            TranslationTextComponent chatmessage = new TranslationTextComponent("multiplayer.disconnect.banned.reason", gameprofilebanentry.getBanReason());
            if (gameprofilebanentry.getBanEndDate() != null) {
                chatmessage.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", DATE_FORMAT.format(gameprofilebanentry.getBanEndDate())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else if (!this.canJoin(gameProfile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, SpigotConfig.whitelistMessage);
        } else if (this.getBannedIPs().isBanned(socketAddress) && !this.getBannedIPs().getBanEntry(socketAddress).hasBanExpired()) {
            IPBanEntry ipbanentry = this.getBannedIPs().getBanEntry(socketAddress);
            TranslationTextComponent chatmessage = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", ipbanentry.getBanReason());
            if (ipbanentry.getBanEndDate() != null) {
                chatmessage.appendSibling(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", DATE_FORMAT.format(ipbanentry.getBanEndDate())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else if (this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(gameProfile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, SpigotConfig.serverFullMessage);
        }
        this.craftServer.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            if (handler != null) {
                handler.disconnect(CraftChatMessage.fromStringOrNull(event.getKickMessage()));
            }
            return null;
        }
        return entity;
    }

    public void updateClient(ServerPlayerEntity entityplayer) {
        entityplayer.sendContainerToPlayer(entityplayer.container);
        ((ServerPlayerEntityBridge) entityplayer).getBukkitEntity().updateScaledHealth();

        entityplayer.connection.sendPacket(new SHeldItemChangePacket(entityplayer.inventory.currentItem));
        int i = entityplayer.world.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO) ? 22 : 23;
        entityplayer.connection.sendPacket(new SEntityStatusPacket(entityplayer, (byte) i));
        float immediateRespawn = entityplayer.world.getGameRules().getBoolean(GameRules.DO_IMMEDIATE_RESPAWN) ? 1.0F : 0.0F;
        entityplayer.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.SET_THUNDER_STRENGTH, immediateRespawn));
    }


    /**
     * @author a
     * @reason a
     */


    @Overwrite
    public void initializeConnectionToPlayer(NetworkManager netManager, ServerPlayerEntity playerIn) throws NoSuchFieldException, IllegalAccessException {

        GameProfile gameprofile = playerIn.getGameProfile();
        PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
        GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
        playerprofilecache.addEntry(gameprofile);
        CompoundNBT compoundnbt = this.readPlayerDataFromFile(playerIn);
        RegistryKey<World> registrykey = compoundnbt != null ? DimensionType.decodeWorldKey(new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt.get("Dimension"))).resultOrPartial(LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD;
        ServerWorld serverworld = this.server.getWorld(registrykey);
        ServerWorld serverworld1;
        if (serverworld == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object) registrykey);
            serverworld1 = this.server.func_241755_D_();
        } else {
            serverworld1 = serverworld;
        }

        playerIn.setWorld(serverworld1);
        playerIn.interactionManager.setWorld((ServerWorld) playerIn.world);
        String s1 = "local";
        if (netManager.getRemoteAddress() != null) {
            s1 = netManager.getRemoteAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName().getString(), s1, playerIn.getEntityId(), playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());

        org.bukkit.entity.Player bukkitPlayer = ((ServerPlayerEntityBridge) playerIn).getBukkitEntity();
        PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.getLocation());
        this.craftServer.getPluginManager().callEvent(ev);
        Location loc = ev.getSpawnLocation();
        serverworld1 = ((CraftWorld) loc.getWorld()).getHandle();
        playerIn.setWorld(serverworld1);
        playerIn.setPositionAndRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        IWorldInfo iworldinfo = serverworld1.getWorldInfo();
        this.setPlayerGameTypeBasedOnOther(playerIn, (ServerPlayerEntity) null, serverworld1);
        ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, netManager, playerIn);
        net.minecraftforge.fml.network.NetworkHooks.sendMCRegistryPackets(netManager, "PLAY_TO_CLIENT");



        GameRules gamerules = serverworld1.getGameRules();
        boolean flag = gamerules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean flag1 = gamerules.getBoolean(GameRules.REDUCED_DEBUG_INFO);


        SJoinGamePacket sJoinGamePacket = new SJoinGamePacket(playerIn.getEntityId(),
                playerIn.interactionManager.getGameType(), playerIn.interactionManager.func_241815_c_(),
                BiomeManager.getHashedSeed(serverworld1.getSeed()), iworldinfo.isHardcore(), this.server.func_240770_D_(),
                this.field_232639_s_, serverworld1.getDimensionType(), serverworld1.getDimensionKey(),
                this.maxPlayers, this.viewDistance, flag1, !flag, serverworld1.isDebug(), serverworld1.isFlatWorld());
        serverplaynethandler.sendPacket(sJoinGamePacket);

//        System.out.println("entityId" + playerIn.getEntityId() + " gameType" + playerIn.interactionManager.getGameType() +
//                " hashedSeed" + BiomeManager.getHashedSeed(serverworld1.getSeed()) + " hardcoreMode" + iworldinfo.isHardcore()
//         + " dimensionKeys" +  this.server.func_240770_D_() + " dynamicRegistries" + this.field_232639_s_
//        + " spawnDimension" + serverworld1.getDimensionType() + " dimension" + serverworld1.getDimensionKey()
//        + " maxPlayers" + this.maxPlayers + " viewDistance" + this.viewDistance +
//                " reducedDebugInfo" + flag1 + " debugMode" + flag + " isFlatWorld" + serverworld1.isFlatWorld());


        serverplaynethandler.sendPacket(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(server.getServerModName())));


        serverplaynethandler.sendPacket(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
        serverplaynethandler.sendPacket(new SPlayerAbilitiesPacket(playerIn.abilities));
        serverplaynethandler.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.OnDatapackSyncEvent((PlayerList) (Object) this, playerIn));
        serverplaynethandler.sendPacket(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        serverplaynethandler.sendPacket(new STagsListPacket(this.server.func_244266_aF()));
        net.minecraftforge.fml.network.NetworkHooks.syncCustomTagTypes(playerIn, this.server.func_244266_aF());
        this.updatePermissionLevel(playerIn);
        playerIn.getStats().markAllDirty();
        playerIn.getRecipeBook().init(playerIn);
        this.sendScoreboard(serverworld1.getScoreboard(), playerIn);
        this.server.refreshStatusNextTick();
        IFormattableTextComponent iformattabletextcomponent;
        if (playerIn.getGameProfile().getName().equalsIgnoreCase(s)) {
            iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined", playerIn.getDisplayName());
        } else {
            iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", playerIn.getDisplayName(), s);
        }

        this.func_232641_a_(iformattabletextcomponent.mergeStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.DUMMY_UUID);
        serverplaynethandler.setPlayerLocation(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), playerIn.rotationYaw, playerIn.rotationPitch);
        this.players.add(playerIn);

        this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
        // this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, playerIn));
        //bukkit
        String joinMessage = CraftChatMessage.fromComponent(iformattabletextcomponent);
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this.craftServer.getPlayer(playerIn), joinMessage);

        this.craftServer.getPluginManager().callEvent(playerJoinEvent);
        //  this.players.remove(playerIn);
        if (!playerIn.connection.getNetworkManager().isChannelOpen()) {
            return;
        }
        joinMessage = playerJoinEvent.getJoinMessage();
        if (joinMessage != null && joinMessage.length() > 0) {
            for (ITextComponent line : CraftChatMessage.fromString(joinMessage)) {
                this.server.getPlayerList().sendPacketToAllPlayers(new SChatPacket(line, ChatType.SYSTEM, Util.DUMMY_UUID));
            }
        }


        for (int i = 0; i < this.players.size(); ++i) {
            playerIn.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        if (playerIn.world == serverworld1 && !serverworld1.getPlayers().contains(playerIn)) {
            serverworld1.addNewPlayer(playerIn);
            this.server.getCustomBossEvents().onPlayerLogin(playerIn);
        }


        this.sendWorldInfo(playerIn, serverworld1);
        if (!this.server.getResourcePackUrl().isEmpty()) {
            playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }

        for (EffectInstance effectinstance : playerIn.getActivePotionEffects()) {
            serverplaynethandler.sendPacket(new SPlayEntityEffectPacket(playerIn.getEntityId(), effectinstance));
        }

        if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10)) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
            ServerWorld finalServerworld = serverworld1;
            Entity entity1 = EntityType.loadEntityAndExecute(compoundnbt1.getCompound("Entity"), serverworld1, (p_217885_1_) -> {
                return !finalServerworld.summonEntity(p_217885_1_) ? null : p_217885_1_;
            });
            if (entity1 != null) {
                UUID uuid;
                if (compoundnbt1.hasUniqueId("Attach")) {
                    uuid = compoundnbt1.getUniqueId("Attach");
                } else {
                    uuid = null;
                }

                if (entity1.getUniqueID().equals(uuid)) {
                    playerIn.startRiding(entity1, true);
                } else {
                    for (Entity entity : entity1.getRecursivePassengers()) {
                        if (entity.getUniqueID().equals(uuid)) {
                            playerIn.startRiding(entity, true);
                            break;
                        }
                    }
                }

                if (!playerIn.isPassenger()) {
                    //     LOGGER.warn("Couldn't reattach entity to player");
                    serverworld1.removeEntity(entity1);

                    for (Entity entity2 : entity1.getRecursivePassengers()) {
                        serverworld1.removeEntity(entity2);
                    }
                }
            }
        }

        playerIn.addSelfToInternalCraftingInventory();
        net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerLoggedIn(playerIn);


    }


    @Inject(method = "sendPlayerPermissionLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getCommandManager()Lnet/minecraft/command/Commands;"))
    private void sendPlayerPermissionLevel(ServerPlayerEntity player, int permLevel, CallbackInfo ci) {
        ((ServerPlayerEntityBridge) player).getBukkitEntity().recalculatePermissions();
    }

}
