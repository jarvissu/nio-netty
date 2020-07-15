###学习NIO与Netty
#### Bio
`com.oppo.usercenter.ojt.bio`:
1. BioServer：单线程BIO的服务器实现
2. BioPlusServer：多线程BIO的服务器实现：基于线程池实现Per Request Per Thread
3. BioClient: 单线程连接服务器的客户端
4. BioPlusClient：模拟高并发，多线程连接服务器的客户端

#### Nio
`com.oppo.usercenter.ojt.nio`:
1. NioServer: 基于Nio的Selector，Channel，Buffer实现的IO多路复用的服务器
2. NioClient：基于Nio的Selector，Channel，Buffer实现的客户端

#### Aio
`com.oppo.usercenter.ojt.aio`:
1. AioServer：基于AIO实现的异步服务器
2. AioClient：基于AIO实现的异步客户端

#### 零拷贝
`com.oppo.usercenter.ojt.zerocopy`
1. StreamCopy: 基于Stream流实现的流式内存拷贝
2. ChannelZeroCopy：基于FileChannel的transferTo或者transferFrom实现的零拷贝
3. FileUtils：测试比较StreamCopy和ChannelZeroCopy拷贝同一个文件时的执行时间

#### reactor模型
`com.oppo.usercenter.ojt.reactor`:
1. 单线程Reactor模型：one_thread
    - `OneThreadReactorServer`: Reactor模型单线程服务器实现，用于注册OP_ACCEPT事件
    - `OneThreadReactor`: Reactor实现，对应一个NIO中的Selector，用于循环监听IO事件（OP_ACCEPT,OP_READ,OP_WRITE,OP_CONNECT)
    - `OneThreadAcceptor`：用于完成与客户端的连接。可以扩展安全认证等操作，并将接收到的SocketChannel注册到Selector上
    - `OneThreadHandler`: 处理客户端的IO事件，并返回响应
2. 多线程Reactor模型：multi_thread
    - `MultiThreadReactorServer`: Reactor模型多线程服务器实现，用于注册OP_ACCEPT事件
    - `MultiThreadReactor`: Reactor实现，内部包装多个SubReactor，每个SubReactor对应一个NIO中的Selector，用于循环监听IO事件（OP_ACCEPT,OP_READ,OP_WRITE,OP_CONNECT)
    - `MultiThreadAcceptor`：用于完成与客户端的连接。可以扩展安全认证等操作，同时将接收到的SocketChannel派发到选择的其中一个SubReactor上
    - `MultiThreadHandler`: 处理客户端的IO事件，并返回响应
    - `SubReactor`: 封装一个Selector，用于循环监听IO事件（OP_ACCEPT,OP_READ,OP_WRITE,OP_CONNECT)
    - `SelectorHolder`: 封装一个Selector，用于解决Selector的select方法和register方法的锁竞争的问题
3. 多线程Reactor模型错误示例：multi_thread_error
   > 没有使用SelectorHolder解决Selector的锁竞争问题，导致selector[0]接收到连接请求后，
   > 无法将接收到的Socketchannel注册到新的Selector上（此时改Selector由于执行select()方法持有了锁）

#### proactor模型
**待学习补充。。。**

#### netty
##### Netty简单介绍
1. Netty高性能的几大原因：
    - 基于NIO实现的IO多路复用（Reactor模型）
    - 大量使用对外内存，不依赖JVM管理内存
    - 支持高性能序列化组件
    - 请求串行化处理，避免多线程下的资源竞争问题
    - 对CAS锁及volatile关键字的合理使用
2. 几大核心组件
    - EventLoop：对应上述Reactor模型中的一个SubReactor，在底层封装了一个NIO的Selector，并且底层是一个单线程的架构。
    - EventLoopGroup：封装多个EventLoop，同时实现了ExecutorService，通过线程池的方式管理多个EventLoop
    - ChannelInboundHandler：处理入站处理器，主要用于接收IO请求
    - ChannelOutboundHandler：出站处理器，主要用于响应IO请求，向客户端（服务器）写入数据。
    - ChannelPipeline：一个双向的链表结构，节点是ChannelHandlerContext对象。其中头结点为HeadContext，尾结点为TailContext
    - ChannelHandlerContext：对应一个Channel和一个ChannelHandler。
    - HeadContext：ChannelPipeline头结点，其中outbound属性为true，inbound属性为false。所有出站处理器的最后一个节点，用于通过Nio的SocketChannel先对端发送数据
    - TailContext：ChannelPipeline尾结点，其中outbound属性为false，inboud属性为true。所有入站处理器的最后一个节点。默认实现为空，即不对请求输入数据执行任何操作。
    - ByteBuf：底层对ByteBuffer的进一步封装，从而提供safe/unsafe，pooled/unpooled, head/direct排列组合共八种不同的实现。以适应各种应用场景
    - Encoder：编码器，一种特殊的ChannelOutboundHandler
    - Decoder：解码器，一种特殊的ChannelInboundHandler
    - NioServerSocketChannel：服务端Channel，对Java原生NIO中的ServerSocketChannel的进一步封装，注册OP_ACCEPT事件。
    - NioSocketChannel：客户端Channel，对Java原生NIO的SocketChannel的进一步封装，可以注册OP_CONNECT，OP_READ，OP_WRITE事件。
##### Netty实战
`com.oppo.usercenter.ojt.netty`
1. Netty入门
     - `NettyServer`：基于Netty实现的服务器，接收用户请求，并将该请求返回给客户端
     - `NettyClientEchoHandler`：服务器端IO事件处理器，实现SimpleChannelInboundHandler，入站处理器
     - `NettyClient`: 基于Netty实现的一个简易客户端，用于向服务器端发送数据（字符串）
     - `NettyClientEchoHandler`：接收服务器响应的数据
2. Netty实现简易Tomcat
3. Netty实现简易Rpc框架                         