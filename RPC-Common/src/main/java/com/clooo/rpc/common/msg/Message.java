package com.clooo.rpc.common.msg;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    /**
     * 根据消息类型字节，获得对应的消息 class
     *
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    /**
     * 请求类型 byte 值
     */
    public static final int PING_MESSAGE = 0;
    public static final int PONG_MESSAGE = 1;
    public static final int RPC_REQUEST_MESSAGE = 2;
    /**
     * 响应类型 byte 值
     */
    public static final int RPC_RESPONSE_MESSAGE = 3;

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(PING_MESSAGE, PingMessage.class);
        messageClasses.put(PONG_MESSAGE, PongMessage.class);
        messageClasses.put(RPC_REQUEST_MESSAGE, RpcRequestMessage.class);
        messageClasses.put(RPC_RESPONSE_MESSAGE, RpcResponseMessage.class);
    }

}
