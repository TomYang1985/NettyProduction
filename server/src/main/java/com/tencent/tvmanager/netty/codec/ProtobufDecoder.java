package com.tencent.tvmanager.netty.codec;

import com.google.protobuf.MessageLite;
import com.tencent.tvmanager.netty.msg.Header;
import com.tencent.tvmanager.netty.msg.KeyRequestProto;
import com.tencent.tvmanager.netty.msg.PayloadProto;
import com.tencent.tvmanager.netty.msg.RecvMsg;
import com.tencent.tvmanager.util.HostUtils;
import com.tencent.tvmanager.util.L;

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

            if (msgType == Header.MsgType.PING) {//心跳ping
                RecvMsg msg = new RecvMsg();
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
                    if (array != null && array.length != bodyLength) {
                        array = new byte[readableLen];
                        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    }
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    offset = 0;
                }

                if (msgType == Header.MsgType.EXCHANGE_KEY) {//交换密钥
                    //解码body获取真正对数据进行加解密的Key
                    byte[] data = KeysManager.getInstance().decryptAESKey(array);

                    if (data == null) {
                        L.print("decryptAESKey error");
                    } else {
                        MessageLite result = decodeBody(msgType, busynissType, data);
                        byte[] bodyAESKey = ((KeyRequestProto.KeyRequest) result).getKeys().toByteArray();
                        String id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());
                        KeysManager.getInstance().putKey(id, bodyAESKey);
                        RecvMsg msg = new RecvMsg();
                        msg.msgType = msgType;
                        msg.data = result;

                        out.add(msg);
                    }
                } else {
                    //AES解码body
                    String id = HostUtils.parseHostPort(ctx.channel().remoteAddress().toString());//获取client的id(host:port)
                    byte[] key = KeysManager.getInstance().getKey(id);//根据id获取对应的key
                    array = KeysManager.getInstance().decryptBody(array, key);//根据key解码数据

                    //反序列化
                    if (array != null) {
                        MessageLite result = decodeBody(msgType, busynissType, array);
                        RecvMsg msg = new RecvMsg();
                        msg.msgType = msgType;
                        msg.data = result;
                        out.add(msg);
                    } else {
                        L.print("decryptBody error");
                    }
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

        } else if (msgType == Header.MsgType.EXCHANGE_KEY) {
            return KeyRequestProto.KeyRequest.getDefaultInstance()
                    .getParserForType().parseFrom(array);
        }


        return null; // or throw exception
    }
}
