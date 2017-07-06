### springboot rabbitMQ RPC

本例是springboot rabbitMQ RPC 例子
client端采用springboot,server端采用Python
rpc采用的应答队列固定为 : app.reply
多个rpc请求走不通的队列,应答队列为1个

运行:
python rpc_server2.py
python rpc_server3.py
启动两个服务端

然后运行单元测试: RPCProducerTests.测试方法使用多线程往两个队列塞入数据,server端处理完数据扔回应答队列.