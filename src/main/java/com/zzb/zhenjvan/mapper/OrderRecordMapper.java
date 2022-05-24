package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.OrderRecord;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/13 16:43
 */
public interface OrderRecordMapper {
    List<OrderRecord> selectByAcc(String selfAccNo);
}
