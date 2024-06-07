package com.clooo.rpc.core.handler;

import com.clooo.rpc.common.msg.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;




import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Slf4j
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();  // 基于线程安全的
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        Promise<Object> promise = PROMISE_MAP.remove(msg.getSequenceId());
        if (promise != null) {
            Object data = msg.getData();
            String error = msg.getError();
            if (error != null) {
                promise.setFailure(new Throwable(error));
            } else {
                promise.setSuccess(data);
            }
        }  else {
//            log.info("promise is null ---- " + PROMISE_MAP);
        }
    }

}
