package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.CustomerActivateInfo;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
public interface CustomerActivateInfoMapper {
    /**
     * 查询激活信息
     * @param selfAccNo
     */
    List<CustomerActivateInfo> selectByAcc(String selfAccNo);
}
