package com.clooo.rpc.common.msg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {

    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private Object data;
    /**
     * 调用接口中的方法名
     */
    private String error;

    public RpcResponseMessage() {
    }

    @Override
    public int getMessageType() {
        return RPC_RESPONSE_MESSAGE;
    }
}
