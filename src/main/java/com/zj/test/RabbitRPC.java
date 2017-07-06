package com.zj.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by zhoujia on 2017/7/5.
 *
 */
@SpringBootApplication
@EnableScheduling
public class RabbitRPC {
    public static void main(String[] args) {

        SpringApplication.run(RabbitRPC.class, args);
    }
}
