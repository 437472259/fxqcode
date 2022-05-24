package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.PositionDetails;

import java.util.List;

/**
 * @author flynn
 * @date 2022/5/15 16:52
 */
public interface PositionDetailsMapper {

    List<PositionDetails> selectByAcc(String accNo);

}
