package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.entity.ProductContractDetail;

import java.util.Date;
import java.util.List;

/**
 * @author flynn
 * @date 2022/5/10 15:14
 */
public interface ProductContractDetailMapper {

    List<ProductContractDetail> selectAll();
    List<ProductContractDetail> selectAll2();

    /**
     * 查询指定日期合约层数
     * @param date
     * @return
     */
    List<ProductContractDetail> selectByDate(Date date);
}
