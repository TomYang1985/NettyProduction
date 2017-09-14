package com.netty.client.codec;

import com.google.protobuf.MessageLite;
import com.netty.client.msg.Header;
import com.netty.client.msg.KeyRequestProto;
import com.netty.client.msg.PayloadProto;
import com.netty.client.msg.PingProto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by robincxiao on 2017/8/18.
 */

public class ProtobufEncoder extends MessageToByteEncoder<MessageLite> {
    private byte[] header = new byte[6];

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, ByteBuf byteBuf) throws Exception {
        byte[] body = messageLite.toByteArray();
        if (body.length > 0) {
            body = encryptBody(messageLite, body);//AES编码
        }
        byte[] header = encodeHeader(messageLite, (short) body.length);

        byteBuf.writeBytes(header);
        if (body != null && body.length > 0) {
            byteBuf.writeBytes(body);
        }
    }

    private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        header[0] = (byte) (bodyLength & 0xff);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        header[2] = Header.PROTOCOL_VERSION;//协议版本
        header[3] = getMsgType(msg);//消息类型（ACK、REQUEST、RESPONSE）
        header[4] = 0;//具体业务类型
        header[5] = 0;//保留

        return header;
    }

    private byte getMsgType(MessageLite msg) {
        if (msg instanceof PingProto.Ping) {
            return Header.MsgType.PING;
        } else if (msg instanceof PayloadProto.Payload) {
            return Header.MsgType.PAYLOAD;
        }else if (msg instanceof KeyRequestProto.KeyRequest) {
            return Header.MsgType.EXCHANGE_KEY;
        }

        return 0;
    }

    private byte[] encryptBody(MessageLite msg, byte[] body) {
        if(msg instanceof KeyRequestProto.KeyRequest) {
            return Algorithm.getInstance().encryptAESKey(body);
        }else {
            return Algorithm.getInstance().encryptBody(body);
        }
    }
}
