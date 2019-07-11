package com.xzj.stu.netty.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * java nio 使用示例
 *
 * @author zhijunxie
 * @date 2019/5/24
 */
public class NIOServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NIOServer.class);

    public static void main(String[] args) throws IOException {
        //1、获取selector选择器
        Selector acceptSelector = Selector.open();
        Selector handSelector = Selector.open();

        new Thread(() -> {
            LOGGER.info("监听线程开始...");
            int count = 0;
            try {
                //2、获取通道  对应IO编程中服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                //3、设置为非阻塞
                listenerChannel.configureBlocking(false);
                //4、绑定连接
                listenerChannel.bind(new InetSocketAddress(8000));
                //5、将通道注册到选择器上，并注册的操作为"接收"操作
                SelectionKey register = listenerChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);

                while (true) {
                    //6、采用轮询的方式，查询获取“准备就绪”的注册过的操作   监测是否有新的连接，这里的1指的是阻塞的时间为1ms
                    if (acceptSelector.select(1) > 0) {
                        count++;
                        LOGGER.info("有新连接 第{}个连接.", count);

                        //7、获取当前选择器中所有注册的选择键（“已经准备就绪的操作”）
                        Set<SelectionKey> set = acceptSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            //8、获取"准备就绪"的事件
                            SelectionKey key = keyIterator.next();

                            //9、判断key是具体的什么事件，如果是"接收就绪"
                            if (key.isAcceptable()) {
                                // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到handSelector
                                try {
                                    LOGGER.info("处理客户端请求...");
                                    //10、获取客户端连接
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    //11、切换为非阻塞模式
                                    clientChannel.configureBlocking(false);
                                    //12、将客户端连接注册到"读取"选择器上
                                    clientChannel.register(handSelector, SelectionKey.OP_READ);
                                    LOGGER.info("客户端请求注册至handSelector");
                                } finally {
                                    //13、移除选择键
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            }

        }, "listenThread").start();


        new Thread(() -> {
            LOGGER.info("处理线程开始...");
            try {
                while (true) {
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为1ms
                    if (handSelector.select(1) > 0) {
                        Set<SelectionKey> set = handSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            keyIterator.remove();
                            if (key.isReadable()) {
                                LOGGER.info("读处理...");
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ByteBuffer buf = ByteBuffer.allocate(1024);
                                // (3) 读取数据以块为单位批量读取
                                clientChannel.read(buf);
                                buf.flip();
                                LOGGER.info("客户端请求参数={}", Charset.defaultCharset().newDecoder().decode(buf).toString());
                                buf.clear();
                                buf.put("服务器收到".getBytes());
                                clientChannel.register(handSelector, SelectionKey.OP_WRITE, buf);//注册写事件
                                LOGGER.info("读处理完成.");
                            } else if (key.isWritable()) {
                                LOGGER.info("写处理...");
                                LOGGER.info("服务端向客户端发送数据...");
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ByteBuffer buf = (ByteBuffer) key.attachment();
                                buf.flip();
                                clientChannel.write(buf);
                                //重新注册读事件
                                clientChannel.register(handSelector, SelectionKey.OP_READ);
                                LOGGER.info("写处理完成.");
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }, "handleThread").start();
    }
}
