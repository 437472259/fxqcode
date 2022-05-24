package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.Log;

import java.util.Date;
import java.util.List;

/**
 * @author flynn
 * @date 2022/5/14 14:39
 */
public interface LogMapper {

    List<Date> selectAllDate();

    List<Log> selectAllByDate(Date date);

}
