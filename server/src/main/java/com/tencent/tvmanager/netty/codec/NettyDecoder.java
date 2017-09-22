package com.tencent.tvmanager.netty.codec;

import com.google.protobuf.MessageLite;
import com.tencent.tvmanager.netty.innermsg.Header;
import com.tencent.tvmanager.netty.innermsg.NettyMessage;
import com.tencent.tvmanager.netty.innermsg.KeyRequestProto;
import com.tencent.tvmanager.netty.innermsg.PayloadProto;
import com.tencent.tvmanager.netty.util.HostUtils;
import com.tencent.tvmanager.util.L;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by robincxiao on 2017/9/15.
 */

public class NettyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 6) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte low = in.readByte();
            byte high = in.readByte();
            short s0 = (short) (low & 0xff);
            short s1 = (short) (high & 0xff);
            s1 <<= 8;
            short bodyLength = (short) (s0 | s1);

            byte protocolVersion = in.readByte();//协议版本
            byte msgType = in.readByte();//消息类型
            byte busynissType = in.readByte();//业务类型
            byte priority = in.readByte();//优先级
            in.readByte();//读保留字节

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < bodyLength) {
                in.resetReaderIndex();
                return;
            }

            if (msgType == Header.MsgType.PING) {//心跳ping
                L.print("recv ping from " + ctx.channel().remoteAddress().toString());
                output(out, msgType, busynissType, priority, null);
            } else if (msgType == Header.MsgType.REQUEST) {
                if (busynissType == Header.BusinessType.REQUEST_APP_LIST
                        || busynissType == Header.BusinessType.REQUEST_TV_UPDATE) {
                    output(out, msgType, busynissType, priority, null);
                }
            } else {// 其它body实体不为空的请求
                ByteBuf bodyByteBuf = in.readBytes(bodyLength);

                byte[] array;
                int readableLen = bodyByteBuf.readableBytes();
                if (bodyByteBuf.hasArray()) {
                    array = bodyByteBuf.array();
                    if (array != null && array.length != bodyLength) {
                        array = new byte[readableLen];
                        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    }
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                }

                if (msgType == Header.MsgType.EXCHANGE_KEY) {//交换密钥
                    //解码body获取真正对数据进行加解密的Key
                    L.d(array);
                    byte[] data = KeysManager.getInstance().decryptAESKey(array);
                    if (data == null) {
                        L.print("decryptAESKey error");
                    } else {
                        MessageLite body = decodeProtobufBody(msgType, busynissType, data);
                        byte[] bodyAESKey = ((KeyRequestProto.KeyRequest) body).getKeys().toByteArray();
                        //将客户端id与bodyAESKey，对应的储存到KeysManager
                        String id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
                        KeysManager.getInstance().putKey(id, bodyAESKey);

                        output(out, msgType, busynissType, priority, body);
                    }
                } else {
                    //解码body
                    String id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());//获取client的id(host:port)
                    byte[] key = KeysManager.getInstance().getKey(id);//根据id获取对应的key
                    array = KeysManager.getInstance().decryptBody(array, key);//根据key解码数据

                    //反序列化
                    if (array != null) {
                        MessageLite body = decodeProtobufBody(msgType, busynissType, array);

                        output(out, msgType, busynissType, priority, body);
                    } else {
                        L.print("decryptBody error");
                    }
                }
            }
        }
    }

    private void output(List<Object> out, byte msgType, byte busynissType, byte priority, MessageLite body) {
        NettyMessage message = new NettyMessage();
        message.msgType = msgType;
        message.businessType = busynissType;
        message.priority = priority;
        message.body = body;

        out.add(message);
    }

    /**
     * 将protobuf序列，反序列为java对象
     *
     * @param msgType
     * @param array
     * @return
     * @throws Exception
     */
    public MessageLite decodeProtobufBody(byte msgType, byte busynissType, byte[] array) throws Exception {
        if (msgType == Header.MsgType.PAYLOAD) {
            return PayloadProto.Payload.getDefaultInstance().
                    getParserForType().parseFrom(array);

        } else if (msgType == Header.MsgType.EXCHANGE_KEY) {
            return KeyRequestProto.KeyRequest.getDefaultInstance()
                    .getParserForType().parseFrom(array);
        }

        return null; // or throw exception
    }
}
