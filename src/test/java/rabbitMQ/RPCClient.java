package rabbitMQ;

/**
 * Created by zhoujia on 2017/7/4.
 */

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;

//RPC调用客户端
public class RPCClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws Exception {
        //• 先建立一个连接和一个通道，并为回调声明一个唯一的'回调'队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(AMQP.PROTOCOL.PORT);
        connection = factory.newConnection();
        channel = connection.createChannel();
        //• 注册'回调'队列，这样就可以收到RPC响应
        replyQueueName = "result-queue";//channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    //发送RPC请求
    public String call(String message) {
        try {
            String response = null;
            String corrId = java.util.UUID.randomUUID().toString();
            //发送请求消息，消息使用了两个属性：replyto和correlationId
            BasicProperties props = new BasicProperties.Builder()
                    .correlationId(corrId).replyTo(replyQueueName).build();
            channel.basicPublish("", requestQueueName, props, message.getBytes());
            //等待接收结果
            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                //检查它的correlationId是否是我们所要找的那个
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response = new String(delivery.getBody());
                    break;
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public void close() throws Exception {
        connection.close();
    }
}