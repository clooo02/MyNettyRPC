package com.clooo.rpc.core.handler;

import com.clooo.rpc.common.msg.RpcRequestMessage;
import com.clooo.rpc.common.msg.RpcResponseMessage;
import com.clooo.rpc.core.provider.ServiceManagement;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;



import java.lang.reflect.Method;

//@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private final ServiceManagement serviceManagement;

    public RpcRequestHandler(ServiceManagement serviceManagement) {
        this.serviceManagement = serviceManagement;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());
        System.out.println("==========" + serviceManagement.getServiceMap());
        try {
            Object service = serviceManagement.getService(msg.getInterfaceName());
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());
            response.setData(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getCause().getMessage();
            response.setError("远程调用错误：" + err);
        } finally {
            ctx.writeAndFlush(response).addListener(future -> {
                if (!future.isSuccess()) {
//                    log.error("响应失败");
                }
            });
        }
    }

}
