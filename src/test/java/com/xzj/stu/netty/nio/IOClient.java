package com.xzj.stu.netty.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @author zhijunxie
 * @date 2019/5/24
 */
public class IOClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOClient.class);

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                int count = 0;
                try {
                    LOGGER.info("{} : 新线程开始....", Thread.currentThread().getName());
                    Socket socket = new Socket("127.0.0.1", 8000);
                    while (count < 5) {
                        count++;
                        LOGGER.info("{} : 请求....", Thread.currentThread().getName());
                        try {
                            socket.getOutputStream().write((Thread.currentThread().getName() + ": [" + new Date() + "] client output: hello world").getBytes());
                            socket.getOutputStream().flush();
                            Thread.sleep(2000);
                        } catch (Exception e) {
                        }
                    }
                } catch (IOException e) {
                }
            }, "xzjThread" + i).start();

            Thread.sleep(5000L);
        }

    }
}
