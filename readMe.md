### springboot rabbitMQ RPC

本例是springboot rabbitMQ RPC 例子
client端采用springboot,server端采用Python
rpc采用的应答队列固定为 : app.reply
多个rpc请求走不通的队列,应答队列为1个

1.启动rabbitMQ服务

2.运行resource文件下的python server服务:<br>
python rpc_server2.py <br>
python rpc_server3.py <br>
启动两个服务端

3.然后运行单元测试: RPCProducerTests.测试方法使用多线程往两个队列塞入数据,server端处理完数据扔回应答队列.