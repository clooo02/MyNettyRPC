package com.clooo.rpc.core.protocol;

import com.clooo.rpc.common.msg.Message;
import com.clooo.rpc.core.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;



import java.util.List;

/**
 * 必须与 LengthFieldBasedFrameDecoder 一起使用，以确保接收的消息是完整的
 */
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        // 1. 4字节的魔数
        byteBuf.writeBytes("CHUN".getBytes());
        // 2. 1字节的版本
        byteBuf.writeByte(1);
        // 3. 1字节的序列化方式 假设jdk为0， json为1
        byteBuf.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1字节的指令类型(与实际的业务类型相关)
        byteBuf.writeByte(message.getMessageType());
        // 5. 4字节的请求序号
        byteBuf.writeInt(message.getSequenceId());

        // 无意义的1字节，目的是使协议的头部对齐填充
        byteBuf.writeByte(0);

        // 6. 消息正文字节数组
        byte[] msgBytes = Config.getSerializerAlgorithm().serializer(message);
        // 6. 4字节消息正文的长度
        byteBuf.writeInt(msgBytes.length);
        // 7. 消息正文的内容
        byteBuf.writeBytes(msgBytes);

//        log.info("编码消息：" + new String(msgBytes));

        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerAlgorithm = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes, 0, length);

        // 序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 具体的消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);

        Message message = algorithm.deserialize(messageClass, bytes);

//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.info("解码消息：" + message.toString());
        list.add(message);
    }
}
