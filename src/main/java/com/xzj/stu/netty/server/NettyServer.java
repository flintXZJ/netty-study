package com.xzj.stu.netty.server;

import com.xzj.stu.netty.common.base.BaseInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/6/19 16:15
 */
public class NettyServer implements IServer {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap serverBootstrap;
    /**
     * boos表示监听端口，创建新连接的线程组
     */
    private NioEventLoopGroup boosGroup;
    /**
     * worker表示处理每一条连接的数据读写的线程组
     */
    private NioEventLoopGroup workerGroup;

    private BaseInitializer baseInitializer;
    /**
     * 服务绑定端口
     */
    private int port;

    private ChannelFuture startFuture;

    public NettyServer(int port, BaseInitializer baseInitializer) {
        this.port = port;
        this.baseInitializer = baseInitializer;
    }

    public void start() throws Exception {
        logger.info("netty server starting...");

        serverBootstrap = new ServerBootstrap();
        boosGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        this.serverBootstrap
                //配置线程模型
                .group(this.boosGroup, this.workerGroup)
                //指定IO模型为NIO  该类中指定该通道处理的事务是SelectionKey.OP_ACCEPT连接事件
                .channel(NioServerSocketChannel.class)
                //主要就是定义后续每条连接的数据读写，业务处理逻辑
                .childHandler(this.baseInitializer)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        //s.bind()中实例化NioServerSocketChannel，初始化，并将其注册到group的boosGroup线程池NIOEventLoopGroup中
        this.startFuture = this.serverBootstrap.bind(this.port).addListener(new PortBindFutureListener()).sync();

        if (!this.startFuture.isSuccess()) {
            logger.info("netty server start failed.");
        } else {
            logger.info("netty server start success.");
        }
    }

    public void stop() throws Exception {
        if (this.startFuture != null && this.startFuture.isSuccess()) {
            logger.info("stop netty server...");
            boolean workerShutdown = false;
            if (workerGroup != null || !workerGroup.isShuttingDown() || !workerGroup.isShutdown()) {
                workerShutdown = workerGroup.shutdownGracefully().sync().isSuccess();
            }

            boolean boosShutdown = false;
            if (boosGroup != null || !boosGroup.isShuttingDown() || !boosGroup.isShutdown()) {
                boosShutdown = boosGroup.shutdownGracefully().sync().isSuccess();
            }

            if (workerShutdown && boosShutdown) {
                this.startFuture = null;
                logger.info("netty server stoped.");
            }
        } else {
            logger.info("netty server is not started.");
        }
    }

    public void restart() throws Exception {
        stop();
        start();
    }

    public Boolean isStarted() {
        if (this.startFuture == null) {
            return false;
        }
        return this.startFuture.isSuccess();
    }
}
