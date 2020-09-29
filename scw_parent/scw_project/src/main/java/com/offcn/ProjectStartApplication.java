package com.offcn;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//启动类
@SpringBootApplication
//开启注册中心服务器
@EnableDiscoveryClient
@MapperScan("com.offcn.project.mapper")
public class ProjectStartApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProjectStartApplication.class);
    }

}
