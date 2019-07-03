package com.xzj.stu.netty.common.base;

import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息处理器（Handler）抽象类
 * <p>
 * 其他各种消息类型的处理器基于此类实现
 *
 * @author zhijunxie
 * @date 2019/7/2 20:54
 */
public abstract class BaseHandler<I> extends SimpleChannelInboundHandler<I> {
    protected static final Logger logger = LoggerFactory.getLogger(BaseHandler.class);
}
