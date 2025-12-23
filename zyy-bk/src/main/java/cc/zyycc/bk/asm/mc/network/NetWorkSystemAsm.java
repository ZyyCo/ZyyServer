package cc.zyycc.bk.asm.mc.netWork;

import cc.zyycc.bk.util.HandshakeGuardHandler;
import cc.zyycc.bk.util.NetworkSystemUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static cc.zyycc.bk.util.NetworkSystemUtil.PROTOCOL_STATE;

public class NetWorkSystemAsm {


    public static void guard(Channel channel){
        channel.attr(PROTOCOL_STATE).set(NetworkSystemUtil.ProtocolState.HANDSHAKE);
        channel.pipeline().addFirst("handshake_guard", new ChannelInboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) {
                // 不该被调用，如果被调用说明有人在你之后插
                throw new IllegalStateException("Pipeline modification during handshake is forbidden");
            }
        });

    }


}
