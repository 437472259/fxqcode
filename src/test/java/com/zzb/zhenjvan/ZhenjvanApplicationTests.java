package com.zzb.zhenjvan;

import com.zzb.zhenjvan.mapper.TestMapper;
import com.zzb.zhenjvan.service.FunctionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ZhenjvanApplicationTests {

    @Resource
    TestMapper testMapper;

    @Resource
    FunctionService functionService;

    @Test
    void contextLoads() {
        functionService.run();
    }




}
