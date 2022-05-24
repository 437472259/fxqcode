package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.UnpopularContractInfo;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/7 16:19
 */
public interface UnpopularContractInfoMapper {

    List<UnpopularContractInfo> selectByAccNo(String selfAccNo);
}
