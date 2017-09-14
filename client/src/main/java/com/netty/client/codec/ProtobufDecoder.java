package com.netty.client.codec;

import com.google.protobuf.MessageLite;
import com.netty.client.msg.Header;
import com.netty.client.msg.KeyResponseProto;
import com.netty.client.msg.PayloadProto;
import com.netty.client.msg.RecvMessage;
import com.netty.client.utils.L;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by robincxiao on 2017/8/18.
 */
public class ProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 5) { // 如果可读长度小于包头长度，退出。
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
            in.readByte();//读保留字节

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < bodyLength) {
                in.resetReaderIndex();
                return;
            }

            if (msgType == Header.MsgType.PONG) {//心跳pong
                L.print("recv server pong");
                RecvMessage msg = new RecvMessage();
                msg.msgType = msgType;

                out.add(msg);
            } else {
                // 读取body
                ByteBuf bodyByteBuf = in.readBytes(bodyLength);

                byte[] array;
                int offset;

                int readableLen = bodyByteBuf.readableBytes();
                if (bodyByteBuf.hasArray()) {
                    array = bodyByteBuf.array();
                    offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
                    if(array != null && array.length != bodyLength){
                        array = new byte[readableLen];
                        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    }
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    offset = 0;
                }

                //AES解码
                array = Algorithm.getInstance().decryptBody(array);

                //反序列化
                if (array != null) {
                    MessageLite result = decodeBody(msgType, busynissType, array);
                    RecvMessage msg = new RecvMessage();
                    msg.msgType = msgType;
                    msg.data = result;
                    out.add(msg);
                } else {
                    L.print("ProtobufDecoder parse array null");
                }
            }
        }
    }

    /**
     * 反序列化java对象
     *
     * @param msgType
     * @param array
     * @return
     * @throws Exception
     */
    public MessageLite decodeBody(byte msgType, byte busynissType, byte[] array) throws Exception {
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
