package com.xzj.stu.netty.server;

import com.xzj.stu.netty.common.base.BaseHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

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
        logger.info("输出内容 = {}", msg);
        ctx.writeAndFlush("netty server reply " + new Date());
    }
}
