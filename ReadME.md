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
`com.oppo.usercenter.ojt.netty`
1. Netty入门
2. Netty实现简易Tomcat
3. Netty实现简易Rpc框架                         