# netty-learning
这是库主要是用来学习和剖析netty，为了更加了解socket方面的知识

## 知识要点

- MessageToMessageEncoder中的acceptOutboundMessage方法，通过反射拿到对象的类型来进行判断是否匹配，如果不匹配略过，向下执行
- bootstrap.connect方法是异步的，可以通过sync来同步操作