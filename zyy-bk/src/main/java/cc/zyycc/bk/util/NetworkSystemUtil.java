package cc.zyycc.bk.util;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import net.minecraft.network.NetworkManager;

import java.lang.reflect.Method;

public class NetworkSystemUtil {
    public static final AttributeKey<ProtocolState> PROTOCOL_STATE =
            AttributeKey.valueOf("protocol_state");

    public static void lock(Channel channel) {
        channel.attr(PROTOCOL_STATE).set(NetworkSystemUtil.ProtocolState.HANDSHAKE);
        channel.pipeline().addFirst("handshake_guard", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                NetworkSystemUtil.ProtocolState state = ctx.channel().attr(PROTOCOL_STATE).get();

                if (state == ProtocolState.HANDSHAKE) {
                    ctx.fireChannelRead(msg);
                    return;
                }
                ctx.fireChannelRead(msg);
            }
        });

    }

    public static void unlock(Channel channel) {
        Runnable task = () -> {
            ChannelPipeline pipeline = channel.pipeline();
            ChannelHandler guard = pipeline.get("handshake_guard");
            if (guard != null) {
                channel.attr(PROTOCOL_STATE).set(ProtocolState.OPEN);
                pipeline.remove(guard);
            }
        };

        if (channel.isRegistered()) {
            channel.eventLoop().execute(task);
        } else {
            task.run();
        }
    }



    public static boolean isAllowedHandshakeHandler(ChannelHandler h) {
        ClassLoader cl = h.getClass().getClassLoader();

        // MC / Netty / Bootstrap
        if (cl == null) return true; // bootstrap
        if (cl == ClassLoader.getSystemClassLoader()) return true;

        String name = h.getClass().getName();

        return name.startsWith("net.minecraft")
                || name.startsWith("io.netty")
                || name.startsWith("com.mojang");
    }


    public enum ProtocolState {
        HANDSHAKE,
        OPEN,
    }
}
