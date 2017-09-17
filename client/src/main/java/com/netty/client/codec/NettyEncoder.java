package com.netty.client.codec;


import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.utils.L;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * Created by robincxiao on 2017/9/15.
 */

public class NettyEncoder extends MessageToByteEncoder<NettyMessage> {
    private byte[] header = new byte[7];

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, ByteBuf byteBuf) throws Exception {
        int bodyLength = 0;
        byte[] body = null;
        if(nettyMessage.body != null){
            body = nettyMessage.body.toByteArray();
            body = encryptBody(nettyMessage.msgType, body);//AES编码
            bodyLength = body.length;
        }

        byte[] header = encodeHeader(nettyMessage, (short) bodyLength);

        byteBuf.writeBytes(header);
        if (body != null && bodyLength > 0) {
            byteBuf.writeBytes(body);
        }
    }

    private byte[] encodeHeader(NettyMessage nettyMessage, short bodyLength) {
        header[0] = (byte) (bodyLength & 0xff);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        header[2] = Header.PROTOCOL_VERSION;//协议版本
        header[3] = nettyMessage.msgType;//消息类型
        header[4] = nettyMessage.busyType;//具体业务类型
        header[5] = nettyMessage.priority;//消息优先级
        header[6] = 0;//保留

        return header;
    }

    private byte[] encryptBody(int messageType, byte[] body) {
        if(messageType == Header.MsgType.EXCHANGE_KEY) {
            return Algorithm.getInstance().encryptAESKey(body);
        }else {
            return Algorithm.getInstance().encryptBody(body);
        }
    }
}
