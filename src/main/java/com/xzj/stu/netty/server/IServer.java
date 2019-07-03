package com.xzj.stu.netty.server;

/**
 * @author zhijunxie
 * @date 2019/7/3 10:43
 */
public interface IServer {
    void start() throws Exception;

    void stop() throws Exception;

    void restart() throws Exception;

    Boolean isStarted();
}
