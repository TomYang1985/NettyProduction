package com.netty.client.codec;

import com.google.protobuf.MessageLite;
import com.netty.client.innermsg.AppActionResponseProto;
import com.netty.client.innermsg.AppListResponseProto;
import com.netty.client.innermsg.CleanResponseProto;
import com.netty.client.innermsg.DeviceInfoResponseProto;
import com.netty.client.innermsg.DownloadResponseProto;
import com.netty.client.innermsg.Header;
import com.netty.client.innermsg.KeyResponseProto;
import com.netty.client.innermsg.NettyMessage;
import com.netty.client.innermsg.PayloadProto;
import com.netty.client.innermsg.ResourceRateResponseProto;
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
                    if (array != null && array.length != bodyLength) {
                        array = new byte[readableLen];
                        bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                    }
                } else {
                    array = new byte[readableLen];
                    bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                }

                //AES解码
                array = KeyManager.getInstance().decryptBody(array);

                //反序列化
                if (array != null) {
                    MessageLite body = decodeProtoBody(msgType, busynissType, array);
                    output(out, msgType, busynissType, priority, body);
                } else {
                    if (msgType == Header.MsgType.EXCHANGE_KEY_RESP) {
                        //密钥交换响应解码失败
                        KeyManager.getInstance().setKeyExchangeStatus(KeyManager.KEY_EXCHANGE_RESPONSE_DECODE_ERROR);
                        L.writeFile("exchange key fail");
                        ctx.channel().close();
                    } else {
                        L.writeFile("NettyDecoder parse array null");
                        ctx.channel().close();
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
     * 反序列化java对象
     *
     * @param msgType
     * @param array
     * @return
     * @throws Exception
     */
    public MessageLite decodeProtoBody(byte msgType, byte busynissType, byte[] array) throws Exception {
        MessageLite body = null;
        switch (msgType) {
            case Header.MsgType.PAYLOAD:
                body = PayloadProto.Payload.getDefaultInstance().getParserForType().parseFrom(array);
                break;
            case Header.MsgType.EXCHANGE_KEY_RESP://交换密钥响应
                body = KeyResponseProto.KeyResponse.getDefaultInstance().
                        getParserForType().parseFrom(array);
                break;
            case Header.MsgType.RESPONSE: {
                switch (busynissType) {
                    case Header.BusinessType.RESPONSE_APP_ADDED://APP安装
                    case Header.BusinessType.RESPONSE_APP_REMOVED://APP删除
                    case Header.BusinessType.RESPONSE_APP_UPDATE://APP更新
                        body = AppActionResponseProto.AppActionResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                    case Header.BusinessType.RESPONSE_APP_LIST://已安装列表
                        body = AppListResponseProto.AppListResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                    case Header.BusinessType.RESPONSE_CLEAN://垃圾清理
                        body = CleanResponseProto.CleanResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                    case Header.BusinessType.RESPONSE_RESOURCE_RATE://资源占用率
                        body = ResourceRateResponseProto.ResourceRateResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                    case Header.BusinessType.RESPONSE_DEVICE_INFO://设备信息
                        body = DeviceInfoResponseProto.DeviceInfoResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                    case Header.BusinessType.RESPONSE_DOWNLOAD://下载
                        body = DownloadResponseProto.DownloadResponse.getDefaultInstance().
                                getParserForType().parseFrom(array);
                        break;
                }
            }
            break;
        }

        return body; // or throw exception
    }
}
