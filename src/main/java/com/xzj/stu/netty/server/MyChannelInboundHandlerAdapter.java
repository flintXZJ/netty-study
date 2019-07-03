package com.xzj.stu.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/2 20:54
 */
public class MyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //收到消息，业务处理逻辑
        logger.info("输出内容 = {}", msg);
    }
}
