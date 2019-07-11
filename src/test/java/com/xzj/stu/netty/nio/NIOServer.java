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
        Selector readSelector = Selector.open();

        new Thread(() -> {
            LOGGER.info("{} : 监控线程开始...", Thread.currentThread().getName());
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
                        LOGGER.info("{} : 新连接 " + count, Thread.currentThread().getName());

                        //7、获取当前选择器中所有注册的选择键（“已经准备就绪的操作”）
                        Set<SelectionKey> set = acceptSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            //8、获取"准备就绪"的事件
                            SelectionKey key = keyIterator.next();

                            //9、判断key是具体的什么事件，如果是"接收就绪"
                            if (key.isAcceptable()) {
                                // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到readSelector
                                try {
                                    //10、获取客户端连接
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    //11、切换为非阻塞模式
                                    clientChannel.configureBlocking(false);
                                    //12、将客户端连接注册到"读取"选择器上
                                    clientChannel.register(readSelector, SelectionKey.OP_READ);
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
            LOGGER.info("{} : 处理线程开始...", Thread.currentThread().getName());
            try {
                while (true) {
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为1ms
                    if (readSelector.select(1) > 0) {// TODO: 2019/5/24 连接断开之后就不会在处理了？？？ 空轮询bug
                        LOGGER.info("{} : 连接可读", Thread.currentThread().getName());
                        Set<SelectionKey> set = readSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();

                            if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    // (3) 读取数据以块为单位批量读取
                                    clientChannel.read(byteBuffer);
                                    byteBuffer.flip();
                                    LOGGER.info(Thread.currentThread().getName() + ": " + Charset.defaultCharset().newDecoder().decode(byteBuffer)
                                            .toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }

                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }, "handleThread").start();
    }
}
