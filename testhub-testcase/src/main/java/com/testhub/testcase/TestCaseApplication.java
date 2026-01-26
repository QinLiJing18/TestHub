package com.testhub.testcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 测试用例服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.testhub"})
@EnableDiscoveryClient
public class TestCaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestCaseApplication.class, args);
        System.out.println("===== TestHub测试用例服务启动成功 (8083) =====");
    }
}
