## NioEventLoopGroup.java

#### selectorProvider = java.nio.channels.spi.SelectorProvider.provider()

#### nThreads = nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads
* DEFAULT_EVENT_LOOP_THREADS = NettyRuntime.availableProcessors() * 2

#### executor = executor == null ? new ThreadPerTaskExecutor(newDefaultThreadFactory()) : executor

#### EventExecutor[] children = new EventExecutor[nThreads];
* children[i] = new NioEventLoop(this, executor, SelectorProvider, SelectStrategyFactory.newSelectStrategy(), RejectedExecutionHandler)

### NioEventLoopGroup 可以有N(N>=1)个NioEventLoop，N个NioEventLoop共同持有一个executor，轮流接收客户端请求

[NioEventLoopGroup UML]()

![NioEventLoopGroup](https://ws1.sinaimg.cn/large/005UybFhly1g4x03873f9j30by0hit8y.jpg)