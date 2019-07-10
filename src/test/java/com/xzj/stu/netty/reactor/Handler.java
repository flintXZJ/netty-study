package com.xzj.stu.netty.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author zhijunxie
 * @date 2019/7/10 17:19
 */
public class Handler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    final SocketChannel channel;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(4096);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector selector, SocketChannel c) throws IOException {
        LOGGER.info("new Handler...");
        channel = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = channel.register(selector, 0);

        //将Handler作为callback对象
        sk.attach(this);

        //第二步,注册Read就绪事件
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    boolean inputIsComplete() {
        /* ... */
        return false;
    }

    boolean outputIsComplete() {

        /* ... */
        return false;
    }

    void process() {
        /* ... */
        return;
    }

    public void run() {
        LOGGER.info("Handler running...");
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void read() throws IOException {
        LOGGER.info("Handler read...");
        channel.read(input);
        if (inputIsComplete()) {
            process();
            state = SENDING;
            // Normally also do first write now
            //第三步,接收write就绪事件
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        LOGGER.info("Handler send...");
        channel.write(output);

        //write完就结束了, 关闭select key
        if (outputIsComplete()) {
            sk.cancel();
        }
    }
}
