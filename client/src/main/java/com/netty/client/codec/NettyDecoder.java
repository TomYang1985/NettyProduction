package com.netty.client.codec;

import com.google.protobuf.MessageLite;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.Header;
import com.netty.client.msg.KeyResponseProto;
import com.netty.client.msg.PayloadProto;
import com.netty.client.utils.L;

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

            if (msgType == Header.MsgType.PONG) {//心跳pong
                L.print("recv server pong");
                output(out, msgType, busynissType, priority, null);
            } else {
                // 读取body
                ByteBuf bodyByteBuf = in.readBytes(bodyLength);

                byte[] array;
                int readableLen = bodyByteBuf.readableBytes();
                if (bodyByteBuf.hasArray()) {
                    array = bodyByteBuf.array();
                    if(array != null && array.length != bodyLength){
                        array = new byte[readableLen];
                        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    }
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                }


                //AES解码
                array = Algorithm.getInstance().decryptBody(array);

                //反序列化
                if (array != null) {
                    MessageLite body = decodeProtoBody(msgType, busynissType, array);
                    output(out, msgType, busynissType, priority, body);
                } else {
                    L.print("NettyDecoder parse array null");
                }
            }
        }
    }

    private void output(List<Object> out, byte msgType, byte busynissType, byte priority, MessageLite body) {
        NettyMessage message = new NettyMessage();
        message.msgType = msgType;
        message.busyType = busynissType;
        message.priority = priority;
        message.body = body;

        out.add(message);
    }

    /**
     * 反序列化java对象
     *
     * @param msgType
     * @param array
     * @return
     * @throws Exception
     */
    public MessageLite decodeProtoBody(byte msgType, byte busynissType, byte[] array) throws Exception {
        if (msgType == Header.MsgType.PAYLOAD) {
            return PayloadProto.Payload.getDefaultInstance().
                    getParserForType().parseFrom(array);

        }else if (msgType == Header.MsgType.EXCHANGE_KEY_RESP) {
            return KeyResponseProto.KeyResponse.getDefaultInstance().
                    getParserForType().parseFrom(array);
        }

        return null; // or throw exception
    }
}
