package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.TradeRecord;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/9 17:32
 */
public interface TradeRecordMapper {

    List<TradeRecord> selectByAcc(String accNo);

}
