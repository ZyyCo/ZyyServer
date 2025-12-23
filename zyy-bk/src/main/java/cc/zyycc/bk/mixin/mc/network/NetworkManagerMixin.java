package cc.zyycc.bk.mixin.mc.network;


import cc.zyycc.bk.util.NetworkSystemUtil;
import cc.zyycc.bk.bridge.network.NetworkManagerBridge;
import com.mojang.authlib.properties.Property;
import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.*;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.UUID;

import static net.minecraft.network.NetworkManager.PROTOCOL_ATTRIBUTE_KEY;


@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin implements NetworkManagerBridge {
    @Shadow
    private INetHandler packetListener;
    @Shadow
    private int field_211395_r;
    @Shadow
    public Channel channel;
    public java.util.UUID spoofedUUID;

    public com.mojang.authlib.properties.Property[] spoofedProfile;

    @Shadow
    public abstract void setConnectionState(ProtocolType newState);


    @Shadow
    public abstract INetHandler getNetHandler();

    @Override
    public UUID bridge$getSpoofedUUID() {
        return spoofedUUID;
    }

    @Override
    public void bridge$setSpoofedUUID(UUID spoofedUUID) {
        this.spoofedUUID = spoofedUUID;
    }


    @Override
    public Property[] bridge$getSpoofedProfile() {
        return spoofedProfile;
    }

    @Override
    public void bridge$setSpoofedProfile(Property[] spoofedProfile) {
        this.spoofedProfile = spoofedProfile;
    }


    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public SocketAddress bridge$getRawAddress() {
        return getRawAddress();
    }


    private String bytesToHex(byte[] remain) {
        StringBuilder hex = new StringBuilder();
        for (byte b : remain) {
            hex.append(Integer.toHexString(0xFF & b));
        }
        return hex.toString();
    }

//    @Overwrite
//    public void disableAutoRead() {
//        this.channel.config().setAutoRead(false);
//    }

//
//    @Overwrite
//    private void dispatchPacket(IPacket<?> inPacket, @Nullable GenericFutureListener<? extends Future<? super Void>> futureListeners) {
//        //this.channel = unwrapChannel(this.channel);
////        if(inPacket instanceof SCustomPayloadPlayPacket || inPacket instanceof SCustomPayloadLoginPacket){
////            System.out.println("[发送] " + inPacket.getClass().getSimpleName() + "当前Channel " + channel);
////        }
//
//
//        ProtocolType protocoltype = ProtocolType.getFromPacket(inPacket);
//        ProtocolType protocoltype1 = this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).get();
//        ++this.field_211395_r;
//        if (protocoltype1 != protocoltype) {
//            this.channel.eventLoop().execute(() -> this.channel.config().setAutoRead(false));
//        }
//
//        if (this.channel.eventLoop().inEventLoop()) {
//            if (protocoltype != protocoltype1) {
//                this.setConnectionState(protocoltype);
//            }
//
//            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
//            if (futureListeners != null) {
//                channelfuture.addListener(futureListeners);
//            }
//
//            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
//        } else {
//            this.channel.eventLoop().execute(() -> {
//                if (protocoltype != protocoltype1) {
//                    this.setConnectionState(protocoltype);
//                }
//                if (inPacket instanceof SCustomPayloadPlayPacket) {
//                    System.out.println("channel= " + this.channel);
//                    System.out.println("channelClass= " + this.channel.getClass());
//                    System.out.println("pipeline" + this.channel.pipeline());
//                }
//
//                ChannelFuture channelfuture1 = this.channel.writeAndFlush(inPacket);
//                if (futureListeners != null) {
//                    channelfuture1.addListener(futureListeners);
//                }
//
//                channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
////                this.setConnectionState(JConsoleContext.ConnectionState.PLAY);
//            });
//        }
//
//    }

//    private static Channel unwrapChannel(Channel channel) {
//        if (isLibProxy(channel)) {
//            return channel; // 原版 channel
//        }
//
//        try {
//            //ProtocolLib
//            Field outer = channel.getClass().getDeclaredField("this$0");
//            outer.setAccessible(true);
//            Object injector = outer.get(channel);
//
//            Field original = injector.getClass().getDeclaredField("originalChannel");
//            original.setAccessible(true);
//            Channel real = (Channel) original.get(injector);
//            return real;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return channel;
//    }
//
//    private static boolean isLibProxy(Channel ch) {
//        return ch.getClass().getName().equals("io.netty.channel.socket.nio.NioSocketChannel") && !ch.toString().contains("protocol");
//    }


    //    @Inject(method = "setConnectionState", at = @At("HEAD"), cancellable = true)
//    private void setConnectionState(ProtocolType newState, CallbackInfo ci) {
//        Channel ch = this.channel;
//        if (ch != null && !ch.config().isAutoRead()) {
//            ch.eventLoop().execute(() -> {
//                if (!ch.config().isAutoRead()) {
//                    System.out.println("[自动读取] " + ch.remoteAddress());
//                    ch.config().setAutoRead(true);
//                }
//            });
//        }
//    }
    @Inject(method = "setNetHandler", at = @At("HEAD"))
    public void setNetHandler(INetHandler handler, CallbackInfo ci) {
        if (channel == null) {
            return;
        }
        if (!(handler instanceof ServerHandshakeNetHandler)) {
            NetworkSystemUtil.unlock(channel);
        }
    }

//    @Inject(method = "channelActive", at = @At("RETURN"))
//    private void channelActive(ChannelHandlerContext p_channelActive_1_, CallbackInfo ci) {
//        NetworkSystemUtil.lock(p_channelActive_1_, packetListener, channel);
//    }
}
