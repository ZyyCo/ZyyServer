package cc.zyycc.bk.asm.bk;


import cc.zyycc.common.asm.HookResult;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import net.minecraft.network.PacketBuffer;

import java.util.List;


public class NettyAsm {

    public void decodeASM3(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_)  {
        byte[] abyte = new byte[3];

        for(int i = 0; i < abyte.length; ++i) {

            abyte[i] = p_decode_2_.readByte();
            if (abyte[i] >= 0) {
                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(abyte));

//                try {
//                    int j = packetbuffer.readVarInt();
//                    if (p_decode_2_.readableBytes() >= j) {
//                        p_decode_3_.add(p_decode_2_.readBytes(j));
//                    }
//
//                    p_decode_2_.resetReaderIndex();
//                } finally {
//                    packetbuffer.release();
//                }

            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");

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
