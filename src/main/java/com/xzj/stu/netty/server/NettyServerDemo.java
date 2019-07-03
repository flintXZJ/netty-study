package com.xzj.stu.netty.server;

import com.xzj.stu.netty.common.StringInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/3 10:30
 */
public class NettyServerDemo {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServerDemo.class);

    public static void main(String[] args) throws Exception {
        StringInitializer stringInitializer = new StringInitializer(new StringHandler());
        NettyServer nettyServer = new NettyServer(8000, stringInitializer);
        nettyServer.start();
    }
}
