package com.xzj.stu.netty.client;

/**
 * @author zhijunxie
 * @date 2019/7/3 17:01
 */
public interface IClient {

    boolean connect() throws Exception;

    boolean isConnected();

    boolean disconnect() throws Exception;

    /**
     * 异步发送消息
     *
     * @param msg
     * @return
     */
    boolean asyncSend(Object msg) throws Exception;

    /**
     * 同步发送消息
     *
     * @param msg
     * @return
     */
    Object syncSend(Object msg) throws Exception;

    void shutdown() throws Exception;
}
