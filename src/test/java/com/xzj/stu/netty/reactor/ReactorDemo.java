package com.xzj.stu.netty.reactor;

/**
 * @author zhijunxie
 * @date 2019/7/10 17:35
 */
public class ReactorDemo {
    public static void main(String[] args) {
        try {
            new Thread(new Reactor(8000)).start();
        } catch (Exception e ){
            e.printStackTrace();
        }
    }
}
