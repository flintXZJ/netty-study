### Reactor模式

#### Netty是典型的Reactor模型结构。Reactor模式也叫反应器模式，大多数IO相关组件如Netty、Redis在使用的IO模式

#### Reactor模式，是基于Java NIO的，在他的基础上，抽象出来两个组件——Reactor和Handler两个组件：
* （1）Reactor：负责响应IO事件，当检测到一个新的事件，将其发送给相应的Handler去处理；新的事件包含连接建立就绪、读就绪、写就绪等。
* （2）Handler:将自身（handler）与事件绑定，负责事件的处理，完成channel的读入，完成处理业务逻辑后，负责将结果写出channel。