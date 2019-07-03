package com.xzj.stu.netty.server;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/7/3 11:09
 */
public class PortBindFutureListener implements GenericFutureListener {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public void operationComplete(Future future) throws Exception {
        if (future.isSuccess()) {
            logger.info("bind port success!");
        } else {
            logger.info("bind port failed!");
        }
    }
}
