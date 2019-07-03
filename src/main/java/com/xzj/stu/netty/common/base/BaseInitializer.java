package com.xzj.stu.netty.common.base;

import com.xzj.stu.netty.server.NettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/2 20:43
 */
public class BaseInitializer extends ChannelInitializer<SocketChannel> {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 自定义消息处理器
     */
    protected BaseHandler<?> handler;

    private int writeTimeOut = 30;
    private int readTimeOut = 30;

    public BaseInitializer(BaseHandler<?> handler) {
        this.handler = handler;
    }

    public BaseHandler<?> getHandler() {
        return handler;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new WriteTimeoutHandler(this.writeTimeOut),
                new ReadTimeoutHandler(this.readTimeOut));
    }
}
