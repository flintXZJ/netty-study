package com.xzj.stu.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/3 10:30
 */
public class NettyServerDemo {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServerDemo.class);

    public static void main(String[] args) throws Exception {
        NettyServer nettyServer = new NettyServer(8000);
        nettyServer.start();
    }
}
