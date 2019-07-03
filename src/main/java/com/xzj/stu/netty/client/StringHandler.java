package com.xzj.stu.netty.client;

import com.xzj.stu.netty.common.base.BaseHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 字符串消息处理器
 *
 * @author zhijunxie
 * @date 2019/7/3 14:11
 */
public class StringHandler extends BaseHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //收到消息，业务处理逻辑
        logger.info("netty server 返回内容 = {}", msg);

        NettyClient client = (NettyClient) ctx.channel().attr(NettyClient.CLIENT).get();
        client.setResponse(msg);
    }
}
