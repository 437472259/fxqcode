package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.CommissionsDetail;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/8 18:55
 */
public interface CommissionsDetailMapper {
    List<CommissionsDetail> selectByAccNo(String selfAccNo);
}
