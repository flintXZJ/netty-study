package com.xzj.stu.netty.client;

import com.xzj.stu.netty.common.StringInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/3 14:33
 */
public class NettyClientDemo {
    protected static final Logger logger = LoggerFactory.getLogger(NettyClientDemo.class);

    public static void main(String[] args) throws Exception {
        logger.info("client starting...");
        StringInitializer stringInitializer = new StringInitializer(new StringHandler());
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8000, stringInitializer);
        nettyClient.connect();

        Object resp = nettyClient.syncSend("client test");
        logger.info("netty server reply = {}", resp);
        nettyClient.shutdown();
    }
}
