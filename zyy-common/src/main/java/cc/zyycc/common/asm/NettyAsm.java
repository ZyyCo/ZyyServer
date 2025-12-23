package cc.zyycc.common.asm;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;
import java.util.ListIterator;

public class NettyAsm {

    public static void decodeASM(ByteToMessageDecoder decoder, ChannelHandlerContext ctx, ByteBuf byteBuffer, List<Object> packets) {
        for (Object input : packets) {
            Class<?> packetClass = input.getClass();
            System.out.println("当前packet来自" + packetClass.getName());
        }

    }

    public static void decodeASM2(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("CHANNEL = " + ctx.channel());
            System.out.println("CHANNELClass = " + ctx.channel().getClass());
            // 关键：只读副本
            ByteBuf view = buf.retainedDuplicate();
            try {
                int packetId = peekPacketId(view);
                System.out.println("DEBUG packetId = " + packetId);
            } finally {
                view.release();
            }
        }





    }


    private static int peekPacketId(ByteBuf buf) {
        int ri = buf.readerIndex();
        try {
            int numRead = 0;
            int result = 0;
            byte read;
            do {
                if (!buf.isReadable()) return -1;
                read = buf.readByte();
                result |= (read & 0x7F) << (7 * numRead++);
            } while ((read & 0x80) != 0);
            return result;
        } finally {
            buf.readerIndex(ri);
        }
    }

}
