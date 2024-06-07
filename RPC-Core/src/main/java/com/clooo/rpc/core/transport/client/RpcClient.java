package com.clooo.rpc.core.transport.client;

import com.clooo.rpc.common.msg.RpcRequestMessage;
import com.clooo.rpc.common.util.SequenceIdGenerator;
import com.clooo.rpc.core.handler.PingHandler;
import com.clooo.rpc.core.handler.RpcResponseHandler;
import com.clooo.rpc.core.protocol.MessageCodecSharable;
import com.clooo.rpc.core.protocol.ProtocolFrameDecoder;
import com.clooo.rpc.core.registry.ServiceDiscoverer;
import com.clooo.rpc.core.registry.ZkClient;
import com.clooo.rpc.core.utils.RpcConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClient {

    private final CuratorFramework client;

    public <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            // 2. Promise对象来接收结果
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseHandler.PROMISE_MAP.put(sequenceId, promise);
            // 3. 发送消息对象
            getChannel().writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
//                    log.error("消息发送失败" + future.cause().toString());
                }
            });
            // 4. 等待结果
            promise.await(5000);
            // 5. 返回结果
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
//                log.error("请求超时");
                throw new RuntimeException(promise.cause());
            }
        });
        return serviceClass.cast(o);
    }

    private volatile Channel channel = null;

    private volatile NioEventLoopGroup group = null;
    private final Object LOCK = new Object();

    public RpcClient() {
        client = ZkClient.createZkClient();
    }

    public Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }

    }

    private void initChannel() {
        group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseHandler RPC_RESPONSE_HANDLER = new RpcResponseHandler();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
//                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                        ch.pipeline().addLast(new PingHandler());
                        ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
                    }
                });
        try {
            ServiceDiscoverer serviceDiscoverer = new ServiceDiscoverer(client, RpcConstant.RPC_SERVICE);
            String instance = serviceDiscoverer.getNextInstance();
            if (instance == null) {
                return;
            }
            log.info("连接到服务器：" + instance);
            String[] hostPort = instance.split(":");
            String host = hostPort[0];
            int port = Integer.parseInt(hostPort[1]);

            channel = bootstrap.connect(host, port).sync().channel();
            channel.closeFuture().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务器已断开连接");
                    if (group != null) {
                        group.shutdownGracefully();
                    }
                    if (client != null) {
                        client.close();
                    }
                }
            });
        } catch (Exception e) {
            log.debug("error: ", e);
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
        if (client != null) {
            client.close();
        }
    }
}
