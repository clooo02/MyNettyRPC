package com.clooo.rpc.core.handler;

import com.clooo.rpc.common.msg.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class PingHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.WRITER_IDLE) {
            // 当长时间没有操作时，向服务端发送一个心跳数据包表明还在正常连接
//            System.out.println("发送一个心跳数据包");
            ctx.writeAndFlush(new PingMessage());
        }
        super.userEventTriggered(ctx, evt);
    }

}
