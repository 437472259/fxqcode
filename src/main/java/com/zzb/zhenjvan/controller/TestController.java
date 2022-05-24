package com.zzb.zhenjvan.controller;

import com.zzb.zhenjvan.cache.GlobalCache;
import com.zzb.zhenjvan.service.FunctionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * name : jmh
 * time : 2022/2/26 12:22
 * @author snjmh
 */

@RestController
@RequestMapping("api")
@Api(value = "客户档案相关的api", tags = {"客户档案相关的api"})
public class TestController {
    @Resource
    private FunctionService functionService;

    @GetMapping("start")
    @ApiOperation(value = "开始分析", notes = "开始分析")
    public String addCategory(){
        functionService.run();
        return "完成";
    }

    @GetMapping("getSize")
    @ApiOperation(value = "查询剩余条数", notes = "查询剩余条数")
    public Integer getSize(){
        if(GlobalCache.accountQueue != null){
            return GlobalCache.accountQueue.size();
        }
        return 0;
    }


    @GetMapping("analyseByCustomer")
    @ApiOperation(value = "单客户分析", notes = "单客户分析")
    public String analyseByCustomer(String customer){
        functionService.analyseByCustomer(customer);
        return "完成";
    }

    @GetMapping("debug")
    @ApiOperation(value = "debug", notes = "debug")
    public String debug(){
        functionService.debug();
        return "完成";
    }

}
