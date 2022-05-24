package com.zzb.zhenjvan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan({"com.zzb.zhenjvan.mapper"})
public class ZhenjvanApplication {

    public static void main(String[] args) {

        SpringApplication.run(ZhenjvanApplication.class, args);
        System.out.println("项目启动完成");
    }

}
