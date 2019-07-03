package com.xzj.stu.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/2 20:43
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new MyChannelInboundHandlerAdapter());
    }
}
