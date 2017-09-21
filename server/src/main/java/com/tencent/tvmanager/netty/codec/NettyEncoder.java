package com.tencent.tvmanager.netty.codec;

import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.util.HostUtils;

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
            body = encryptBody(channelHandlerContext, body);//AES编码
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
        header[4] = nettyMessage.businessType;//具体业务类型
        header[5] = nettyMessage.priority;//消息优先级
        header[6] = 0;//保留

        return header;
    }

    private byte[] encryptBody(ChannelHandlerContext channelHandlerContext, byte[] body) {
        String id = HostUtils.parseHostPort(channelHandlerContext.channel().remoteAddress().toString());//获取client的id(host:port)
        byte[] key = KeysManager.getInstance().getKey(id);//根据id获取对应的key
        return KeysManager.getInstance().encryptBody(body, key);//加密body数据
    }
}
