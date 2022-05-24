package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.AppTradeRecord;

import java.util.Date;
import java.util.List;

/**
 * @author flynn
 * @date 2022/5/9 19:14
 */
public interface AppTradeRecordMapper {

    List<AppTradeRecord> selectByAccNo(String accNo);

    List<AppTradeRecord> selectByDate(Date date);
}
