package com.xzj.stu.netty.client;

import com.xzj.stu.netty.common.base.BaseInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/3 19:56
 */
public abstract class AbstractNettyClient implements IClient {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractNettyClient.class);

    protected String host;
    protected int port;
    protected Channel channel;
    private Bootstrap bootstrap;
    private NioEventLoopGroup workGroup;
    private BaseInitializer baseInitializer;
    private Object response;
    /**
     * 连接状态
     */
    private ChannelFuture connectFuture;

    public AbstractNettyClient(String host, int port, BaseInitializer baseInitializer) {
        this.host = host;
        this.port = port;
        this.baseInitializer = baseInitializer;
    }

    public BaseInitializer getBaseInitializer() {
        return baseInitializer;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public boolean connect() throws Exception {
        this.bootstrap = new Bootstrap();
        this.workGroup = new NioEventLoopGroup();

        this.bootstrap.group(this.workGroup)
                .channel(NioSocketChannel.class)
                .handler(this.baseInitializer)
                .option(ChannelOption.SO_KEEPALIVE, true);

        int retryCount = 0;
        while (retryCount < 3) {
            synchronized (this) {
                connectFuture = bootstrap.connect(this.host, this.port);
            }

            if (connectFuture.await().isSuccess()) {
                channel = connectFuture.channel();
                // 把客户端实例存储到channel中
                channel.attr(NettyClient.CLIENT).set(this);
                break;
            }
            retryCount++;
            Thread.sleep(100L);
        }

        return connectFuture.isSuccess();
    }

    public boolean isConnected() {
        if (connectFuture == null) {
            return false;
        }
        return connectFuture.channel().isActive();
    }

    public boolean disconnect() throws Exception {
        if (channel == null) {
            logger.info("client server did not connet.");
            return true;
        }
        if (!channel.isActive()) {
            logger.info("client server war already disconneted.");
            return true;
        }
        ChannelFuture future = channel.close().sync();
        if (future.isSuccess()) {
            logger.info("client server success disconneted.");
            return true;
        } else {
            logger.info("client server failed disconneted.");
            return false;
        }

    }

    public void shutdown() throws Exception {
        disconnect();
        if (workGroup != null && !workGroup.isShuttingDown() && !workGroup.isShutdown()) {
            if (workGroup.shutdownGracefully().sync().isSuccess()) {
                connectFuture = null;
                logger.info("The connection with server {}:{} gracefully shutdown", host, port);
            }
        }
    }

    protected ChannelFuture send(Object msg) throws Exception {
        // Client正在连接中，直接退出
        if (connectFuture != null && !connectFuture.isDone()) {
            return connectFuture;
        }
        // 如果Client没有连接过 or 通道已经断开，先尝试连接
        if (channel == null || !channel.isActive()) {
            if (!connect()) {
                return connectFuture;
            }
        }

        ChannelFuture future = channel.writeAndFlush(msg).sync();
        if (!future.isSuccess()) {
            logger.info("发送失败");
        }
        return future;
    }
}
