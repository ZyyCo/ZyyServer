package cc.zyycc.bk.asm.mc.netWork;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;

public class NetworkSystemUtil {
    public static final AttributeKey<ProtocolState> PROTOCOL_STATE =
            AttributeKey.valueOf("protocol_state");
    public static void lock(Channel channel){
        channel.attr(PROTOCOL_STATE).set(ProtocolState.HANDSHAKE);
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get("handshake_guard") == null) {
            pipeline.addFirst(
                    "handshake_guard",
                    new HandshakeGuardHandler()
            );
        }
    }

    public static void unlock(Channel channel) {
        channel.attr(PROTOCOL_STATE).set(ProtocolState.OPEN);

        ChannelPipeline pipeline = channel.pipeline();
        ChannelHandler guard = pipeline.get("handshake_guard");
        if (guard != null) {
            pipeline.remove(guard);
        }
    }


    public enum ProtocolState {
        HANDSHAKE,
        OPEN,
    }
}
