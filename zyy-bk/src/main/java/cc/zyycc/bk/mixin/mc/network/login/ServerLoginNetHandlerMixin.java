package cc.zyycc.bk.mixin.mc.network.login;


import cc.zyycc.bk.bridge.SimpleBridge;
import cc.zyycc.bk.bridge.network.NetworkManagerBridge;
import cc.zyycc.bk.bridge.network.login.ServerLoginNetHandlerBridge;
import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import cc.zyycc.bk.bridge.server.management.PlayerListBridge;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@Mixin(ServerLoginNetHandler.class)
public abstract class ServerLoginNetHandlerMixin implements ServerLoginNetHandlerBridge {
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    private GameProfile loginGameProfile;
    @Shadow
    @Final
    public NetworkManager networkManager;

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private ServerLoginNetHandler.State currentLoginState;
    @Shadow
    @Final
    private byte[] verifyToken;
    @Shadow
    private SecretKey secretKey;

    @Shadow
    protected abstract GameProfile getOfflineProfile(GameProfile original);

    @Shadow
    public abstract void disconnect(ITextComponent reason);

    @Shadow
    public abstract String getConnectionInfo();

    @Shadow
    @Final
    private static AtomicInteger AUTHENTICATOR_THREAD_ID;
    @Shadow private ServerPlayerEntity player;

    public String hostname;
    @Override
    public String bridge$getHostname() {
        return hostname;
    }

    @Override
    public void bridge$setHostname(String hostname) {
        this.hostname = hostname;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tryAcceptPlayer() {
        ServerPlayerEntity entity = ((PlayerListBridge) this.server.getPlayerList()).bridge$canPlayerLogin(this.networkManager.getRemoteAddress(), this.loginGameProfile, (ServerLoginNetHandler) (Object) this);
        if (entity == null) {
           // this.disconnect(iTextComponent);
        } else {
            this.currentLoginState = ServerLoginNetHandler.State.ACCEPTED;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.networkManager.isLocalChannel()) {
                this.networkManager.sendPacket(new SEnableCompressionPacket(this.server.getNetworkCompressionThreshold()), (p_210149_1_) -> {
                    this.networkManager.setCompressionThreshold(this.server.getNetworkCompressionThreshold());
                });
            }

            this.networkManager.sendPacket(new SLoginSuccessPacket(this.loginGameProfile));
            ServerPlayerEntity serverplayerentity = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
            if (serverplayerentity != null) {
                this.currentLoginState = ServerLoginNetHandler.State.DELAY_ACCEPT;
                this.player = entity;
            } else {
                this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, entity);
            }
        }
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public void processLoginStart(CLoginStartPacket packetIn) {
        Validate.validState(this.currentLoginState == ServerLoginNetHandler.State.HELLO, "Unexpected hello packet");
        this.loginGameProfile = packetIn.getProfile();
        if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
            this.currentLoginState = ServerLoginNetHandler.State.KEY;
            this.networkManager.sendPacket(new SEncryptionRequestPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.verifyToken));
        } else {
           // this.currentLoginState = ServerLoginNetHandler.State.NEGOTIATING;
            (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
                public void run() {
                    try {
                        initUUID();
                        fireEvents();
                    } catch (Exception ex) {
                        System.err.println("Failed to verify username!");
//                        LoginListener.this.disconnect("Failed to verify username!");
//                        LoginListener.this.server.server.getLogger().log(Level.WARNING, "Exception verifying " + LoginListener.this.i.getName(), ex);
                    }

                }
            }).start();
        }

    }

    /**
     * @author a
     * @reason a
     */
    @Overwrite
    public void processEncryptionResponse(CEncryptionResponsePacket packetIn) {
        Validate.validState(this.currentLoginState == ServerLoginNetHandler.State.KEY, "Unexpected key packet");
        PrivateKey privatekey = this.server.getKeyPair().getPrivate();

        final String s;
        try {
            if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey))) {
                throw new IllegalStateException("Protocol error");
            }

            this.secretKey = packetIn.getSecretKey(privatekey);
            Cipher cipher = CryptManager.createNetCipherInstance(2, this.secretKey);
            Cipher cipher1 = CryptManager.createNetCipherInstance(1, this.secretKey);
            s = (new BigInteger(CryptManager.getServerIdHash("", this.server.getKeyPair().getPublic(), this.secretKey))).toString(16);
            this.currentLoginState = ServerLoginNetHandler.State.AUTHENTICATING;
            this.networkManager.func_244777_a(cipher, cipher1);
        } catch (CryptException cryptexception) {
            throw new IllegalStateException("Protocol error", cryptexception);
        }

        Thread thread = new Thread(SidedThreadGroups.SERVER, "User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
            public void run() {
                GameProfile gameprofile = loginGameProfile;

                try {
                    loginGameProfile = server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s, this.getAddress());

                    if (loginGameProfile != null) {
                        //  ServerLoginNetHandler.LOGGER.info("UUID of player {} is {}", ServerLoginNetHandler.this.loginGameProfile.getName(), ServerLoginNetHandler.this.loginGameProfile.getId());
                       currentLoginState = ServerLoginNetHandler.State.NEGOTIATING;
                        if (!networkManager.isChannelOpen()) {
                            return;
                        }
                        fireEvents();
                    }else


                    if (server.isSinglePlayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        loginGameProfile = getOfflineProfile(gameprofile);
                        currentLoginState = ServerLoginNetHandler.State.NEGOTIATING;
                    } else {
                        disconnect(new TranslationTextComponent("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object) gameprofile.getName());
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (server.isSinglePlayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        loginGameProfile = getOfflineProfile(gameprofile);
                        currentLoginState = ServerLoginNetHandler.State.NEGOTIATING;
                    } else {
                        disconnect(new TranslationTextComponent("multiplayer.disconnect.authservers_down"));
                        LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketaddress = networkManager.getRemoteAddress();
                return server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
            }

        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public void fireEvents() throws Exception {
        String playerName = this.loginGameProfile.getName();
        InetAddress address = ((InetSocketAddress) this.networkManager.getRemoteAddress()).getAddress();
        UUID uniqueId = this.loginGameProfile.getId();
        final CraftServer server = ((SimpleBridge) this.server).bridge$getCraftServer();
        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
        server.getPluginManager().callEvent(asyncEvent);
        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
            if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
            }

            Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                protected PlayerPreLoginEvent.Result evaluate() {
                    server.getPluginManager().callEvent(event);
                    return event.getResult();
                }
            };
            ((MinecraftServerBridge) this.server).bridge$queuedProcess(waitable);
            if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                this.disconnect(new StringTextComponent(event.getKickMessage()));
                return;
            }
        } else if (asyncEvent.getLoginResult() != org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.disconnect(new StringTextComponent(asyncEvent.getKickMessage()));
            return;
        }

        LOGGER.info("UUID of player {} is {}", loginGameProfile.getName(), loginGameProfile.getId());
        this.currentLoginState = ServerLoginNetHandler.State.NEGOTIATING;
    }

    @Deprecated
    public void disconnect(final String s) {
        try {
            final ITextComponent ichatbasecomponent = new StringTextComponent(s);
            LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), s);
            this.networkManager.sendPacket(new SDisconnectLoginPacket(ichatbasecomponent));
            this.networkManager.closeChannel(ichatbasecomponent);
        } catch (Exception exception) {
//            LOGGER.error("Error whilst disconnecting player", exception);
        }
    }

    public void initUUID() {
        UUID uuid;
        if (((NetworkManagerBridge) this.networkManager).bridge$getSpoofedUUID() != null) {
            uuid = ((NetworkManagerBridge) this.networkManager).bridge$getSpoofedUUID();
        } else {
            uuid = PlayerEntity.getOfflineUUID(this.loginGameProfile.getName());
        }

        this.loginGameProfile = new GameProfile(uuid, this.loginGameProfile.getName());
        if (((NetworkManagerBridge) this.networkManager).bridge$getSpoofedProfile() != null) {
            Property[] spoofedProfile;
            for (int length = (spoofedProfile = ((NetworkManagerBridge) this.networkManager).bridge$getSpoofedProfile()).length, i = 0; i < length; ++i) {
                final Property property = spoofedProfile[i];
                this.loginGameProfile.getProperties().put(property.getName(), property);
            }
        }

    }

}
