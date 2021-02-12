# ZooKeeper
## 监听器
### 应用
监听器主要负责监听节点数据变化（get path [Target]）和监听子节点增减变化（ls path [Target]）
### 原理
1. main()线程
2. 在main()线程创建ZooKeeper客户端，创建两个线程
- send ：负责网络连接通信
- event：负责监听
3. 通过send线程将注册的监听事件发送给ZooKeeper
4. 在ZooKeeper的监听列表中将注册的监听事件添加到列表
5. ZooKeeper监听到数据或路径的变化，就将消息发送给event
6. event线程内部调用了process()方法
