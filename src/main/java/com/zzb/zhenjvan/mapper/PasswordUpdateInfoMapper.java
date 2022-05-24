package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.PasswordUpdateInfo;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
public interface PasswordUpdateInfoMapper {
    List<PasswordUpdateInfo> selectByAccNo(String selfAccNo);
}
