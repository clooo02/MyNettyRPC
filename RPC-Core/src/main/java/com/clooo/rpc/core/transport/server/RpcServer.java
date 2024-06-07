package com.clooo.rpc.core.transport.server;


import com.clooo.rpc.core.handler.PongHandler;
import com.clooo.rpc.core.handler.RpcRequestHandler;
import com.clooo.rpc.core.protocol.MessageCodecSharable;
import com.clooo.rpc.core.protocol.ProtocolFrameDecoder;
import com.clooo.rpc.core.registry.ServiceRegistrar;
import com.clooo.rpc.core.registry.ZkClient;
import com.clooo.rpc.core.utils.RpcConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;

public class RpcServer extends AbstractRpcServer {
    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    private final String host;
    private final Integer port;
    private CuratorFramework client;

    public RpcServer() {
        this.host = "0.0.0.0";
        this.port = 8888;
        scanServices();
    }

    public RpcServer(String host, Integer port) {
        this.host = host;
        this.port = port;
        client = ZkClient.createZkClient();
        scanServices();
    }

    @Override
    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcRequestHandler RPC_REQUEST_HANDLER = new RpcRequestHandler(serviceManagement);
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder());
//                            pipeline.addLast(LOGGING_HANDLER);
                            pipeline.addLast(MESSAGE_CODEC);
                            pipeline.addLast(new IdleStateHandler(5, 0, 0));
                            pipeline.addLast(new PongHandler());
                            pipeline.addLast(RPC_REQUEST_HANDLER);
                        }
                    })
                    .bind(host, port);
            channel = channelFuture.sync().channel();

            // 注册到zookeeper中
            ServiceRegistrar serviceRegistrar = new ServiceRegistrar(client);
            serviceRegistrar.registerService(RpcConstant.RPC_SERVICE, host, port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (boss != null && worker != null) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
        if (client != null) {
            client.close();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
