package com.zj.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhoujia on 2017/7/5.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = RPCProducer.class)
//@WebAppConfiguration

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitRPC.class)
@DirtiesContext
public class RPCProducerTest {

    private Logger logger = LoggerFactory.getLogger(RPCProducerTest.class);

    @Autowired
    private RPCProducer rpcProducer;

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @Test
    public void simpTest() {
        String s = rpcProducer.sendMsgAsync("1", RabbitConfig.ROUTING_KEY_NAME1);

    }


    @Test
    public void RPCProducerTests() {

        Random random = new Random();
        Map<String, Future<String>> resultMap = new HashMap<>();
        Thread t1 = new Thread(()->{
            for(int i=0;i<1000;i++) {
                int randomInt = random.nextInt();
                Future<String> submit = pool.submit(() -> rpcProducer.sendMsgAsync(String.valueOf(randomInt),RabbitConfig.ROUTING_KEY_NAME1));
                resultMap.put(String.valueOf(randomInt), submit);
            }
        });

        Thread t2 = new Thread(()->{
            for(int i=0;i<1000;i++) {
                int randomInt = random.nextInt();
                Future<String> submit = pool.submit(() -> rpcProducer.sendMsgAsync(String.valueOf(randomInt),RabbitConfig.ROUTING_KEY_NAME2));
                resultMap.put(String.valueOf(randomInt), submit);
            }
        });
        t1.start();
        t2.start();

        try {
            resultMap.forEach((k, v) -> {
                try {
                    System.out.println(k + " " + v.get());
                } catch (InterruptedException e) {
                    logger.error("InterruptedException: ",e);
                } catch (ExecutionException e) {
                    logger.error("ExecutionException: ",e);
                }
            });

            Thread.sleep(100000);//测试线程是主线程,因为是异步,需要等回调执行完毕.主线程挂掉异步返回将抛异常
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
