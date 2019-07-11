package com.xzj.stu.netty.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zhijunxie
 * @date 2019/5/24
 */
public class NIOClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NIOClient.class);

    public static void main(String[] args) throws Exception {
        NIOClient nioClient = new NIOClient();
        nioClient.request();
        Thread.sleep(10000L);
        nioClient.request();
    }

    private void request() {
        new Thread(new Client()).start();
    }

    private class Client implements Runnable {
        @Override
        public void run() {
            try {
                Selector selector = Selector.open();

                SocketChannel clientSocket = SocketChannel.open();
                clientSocket.configureBlocking(false);
                clientSocket.connect(new InetSocketAddress("127.0.0.1", 8000));

                clientSocket.register(selector, SelectionKey.OP_CONNECT);

                while (true) {
                    if (selector.select() > 0) {
                        Set<SelectionKey> set = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            keyIterator.remove();

                            if (key.isConnectable()) {
                                LOGGER.info("client connect...");
                                SocketChannel channel = (SocketChannel) key.channel();
                                // 判断此通道上是否正在进行连接操作。
                                // 完成套接字通道的连接过程。
                                if (channel.isConnectionPending()) {
                                    channel.finishConnect();
                                    LOGGER.info("client connected! request server...");
                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                                    String request = "Hello, Server " + new Date();
                                    buffer.put(request.getBytes());
                                    buffer.flip();
                                    channel.write(buffer);
                                }
                                channel.register(selector, SelectionKey.OP_READ);
                            }
                            if (key.isReadable()) {
                                LOGGER.info("client read...");
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                // (3) 读取数据以块为单位批量读取
                                channel.read(byteBuffer);
                                byteBuffer.flip();
                                LOGGER.info("server response={}", Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                            }
                            if (key.isWritable()) {
                                LOGGER.info("client wirte...");
                                SocketChannel sc = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                buffer.put("client wirte.".getBytes());
                                buffer.flip();
                                sc.write(buffer);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
