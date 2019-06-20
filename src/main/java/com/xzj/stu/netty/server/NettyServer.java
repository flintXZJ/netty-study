package com.xzj.stu.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhijunxie
 * @date 2019/6/19 16:15
 */
public class NettyServer {
    protected static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //boos表示监听端口，创建新连接的线程组
        NioEventLoopGroup boos = new NioEventLoopGroup();
        //worker表示处理每一条连接的数据读写的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap
                //配置线程模型
                .group(boos, worker)
                //指定IO模型为NIO
                .channel(NioServerSocketChannel.class)
                //主要就是定义后续每条连接的数据读写，业务处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //NioServerSocketChannel和NioSocketChannel的概念可以和BIO编程模型中的ServerSocket以及Socket两个概念对应上
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
                                logger.info("输出内容 = {}", msg);
                            }
                        });
                    }
                })
                .bind(8000).addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    logger.info("端口绑定成功!");
                } else {
                    logger.info("端口绑定失败!");
                }
            }
        });
    }
}
