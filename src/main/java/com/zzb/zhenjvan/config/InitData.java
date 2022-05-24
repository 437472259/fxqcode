package com.zzb.zhenjvan.config;


import com.zzb.zhenjvan.cache.GlobalCache;
import com.zzb.zhenjvan.constant.GlobalConstant;

import com.zzb.zhenjvan.entity.WhiteIpMac;
import com.zzb.zhenjvan.mapper.AnalysisMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;


/**
 * @author lisai
 * @title: InitData
 * @projectName aml
 * @description:
 * @date 2020/8/10 15:11
 */
@Component
@Slf4j
public class InitData implements ApplicationRunner {

    @Resource
    AnalysisMapper analysisMapper;

    @Override
    public void run(ApplicationArguments applicationArguments) {

        /**
         * 插入IP-MAC白名单
         */
        GlobalConstant.WHITE_IP_MAC = analysisMapper.selectAllWhiteIpMac();
        for (WhiteIpMac whiteIpMac : GlobalConstant.WHITE_IP_MAC) {
            GlobalCache.macWitheSet.add(whiteIpMac.getMac().replace(":",""));
        }
        log.info("=========================项目启动成功================================");

    }

}
