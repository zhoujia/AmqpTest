package com.zj.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhoujia on 2017/7/5.
 *
 */
@Service
//@Component
public class RPCProducer {
    private static final Logger logger = LoggerFactory.getLogger(RPCProducer.class);

    //@Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;

    public RPCProducer(AsyncRabbitTemplate asyncRabbitTemplate) {

        this.asyncRabbitTemplate = asyncRabbitTemplate;
    }

    public String sendMsgAsync(String msg,String routingKey) {
        AsyncRabbitTemplate.RabbitConverterFuture<byte[]> future =
                asyncRabbitTemplate.convertSendAndReceive(RabbitConfig.FIBO_CALCULATOR_EXCHANGE_NAME, routingKey, msg);

        future.addCallback(new ListenableFutureCallback<byte[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                logger.error("--------exception-------");
            }

            @Override
            public void onSuccess(byte[] result) {
                System.out.println();
                try {
                    logger.info("result == {}", new String(result,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        });
        try {
            byte[] bytes = future.get();
            return new String(bytes,"utf-8");
        } catch (InterruptedException e) {
            logger.error("InterruptedException ",e);
        } catch (ExecutionException e) {
            logger.error("ExecutionException ",e);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException ",e);
        }
        return null;
    }

    public void test() {
        System.out.println("test");
    }

}
