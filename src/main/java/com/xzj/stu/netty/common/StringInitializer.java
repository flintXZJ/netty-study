package com.xzj.stu.netty.common;

import com.xzj.stu.netty.common.base.BaseHandler;
import com.xzj.stu.netty.common.base.BaseInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author zhijunxie
 * @date 2019/7/3 14:22
 */
public class StringInitializer extends BaseInitializer {

    public StringInitializer(BaseHandler<?> handler) {
        super(handler);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);
        ch.pipeline().addLast(
                new StringEncoder(),
                new StringDecoder(),
                this.handler
        );
    }
}
