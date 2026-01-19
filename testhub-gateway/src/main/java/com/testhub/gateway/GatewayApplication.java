package com.testhub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关启动类
 *
 * 作用：所有客户端请求的统一入口
 *
 * 主要功能：
 * 1. 路由转发：根据路径将请求转发到不同的微服务
 * 2. JWT认证：验证Token，提取用户信息
 * 3. 限流熔断：保护后端服务不被压垮
 * 4. 日志记录：记录每个请求的信息
 *
 * @author TestHub Team
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient  // 启用Nacos服务发现
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("   TestHub API网关启动成功！");
        System.out.println("   端口: 8080");
        System.out.println("   文档地址: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
