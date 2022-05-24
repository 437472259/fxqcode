package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 锁仓记录
 * @author flynn
 * @date 2022/5/15 16:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockedDealNbr {

    /**
     * 锁单成交编号
     */
    private Set<String>  dealNbrSet;

    /**
     * 锁单成交金额
     */
    private BigDecimal tradeAmount;

}
