package com.xzj.stu.netty.client;

import com.xzj.stu.netty.common.base.BaseInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/6/19 16:41
 */
public class NettyClient extends AbstractNettyClient {
    // Netty框架下用来存储client实例的属性键值
    public static final AttributeKey<IClient> CLIENT = AttributeKey.newInstance("client");

    protected static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public NettyClient(String host, int port, BaseInitializer baseInitializer) {
        super(host, port, baseInitializer);
    }

    public boolean asyncSend(Object msg) throws Exception {
        ChannelFuture future = send(msg);
        if (!future.isSuccess()) {
            logger.info("异步发送失败");
            return false;
        }
        logger.info("异步发送成功");
        return future.isSuccess();
    }


    public Object syncSend(Object msg) throws Exception {
        // 清空response对象
        setResponse(null);

        ChannelFuture future = send(msg);

        if (!future.isSuccess()) {
            logger.info("同步发送失败");
            throw new Exception("同步发送失败");
        }

        int timeout = 0;
        while (getResponse() == null) {
            Thread.sleep(100L);
            timeout += 100;
            if (timeout > getBaseInitializer().getReadTimeOut() * 1000) {
                disconnect();
                logger.info("read time out.");
                throw new Exception("read time out.");
            }
            if (!channel.isActive() && getResponse() == null) {
                logger.info("netty server disconnected, no response.");
                throw new Exception("netty server disconnected, no response.");
            }
        }

        disconnect();

        return getResponse();
    }


}
