package com.zj.test;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Created by zhoujia on 2017/7/5.
 */
public class Receiver implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("=================");
        System.out.println(message);
        System.out.println("=================");
    }
}