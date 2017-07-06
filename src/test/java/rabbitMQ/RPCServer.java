package rabbitMQ;

/**
 * Created by zhoujia on 2017/7/4.
 *
 */
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.security.MessageDigest;

//RPC调用服务端
public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public static void main(String[] args) throws Exception {
        //• 先建立连接、通道，并声明队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        //•可以运行多个服务器进程。通过channel.basicQos设置prefetchCount属性可将负载平均分配到多台服务器上。
        channel.basicQos(1);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //打开应答机制autoAck=false
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        System.out.println(" [x] Awaiting RPC requests");
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties.Builder()
                    .correlationId(props.getCorrelationId()).build();
            String message = new String(delivery.getBody());
            System.out.println(" [.] getMd5String(" + message + ")");
            String response = message + ":" +getMd5String(message);
            //返回处理结果队列
            channel.basicPublish("", props.getReplyTo(), replyProps,
                    response.getBytes());
            //发送应答
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
    // 模拟RPC方法 获取MD5字符串
    public static String getMd5String(String str) {
        MessageDigest md5 = null;
        try {
            Thread.sleep(Integer.parseInt(str));
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}