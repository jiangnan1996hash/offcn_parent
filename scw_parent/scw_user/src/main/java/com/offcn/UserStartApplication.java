package com.offcn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//注册中心能够发现，扫描到这个服务。
@EnableDiscoveryClient
@MapperScan("com.offcn.user.mapper")

public class UserStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserStartApplication.class);
    }
}
