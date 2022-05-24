package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.LossDetail;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
public interface LossDetailMapper {

    List<LossDetail> selectByAccNo(String selfAccNo);
}
