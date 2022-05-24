package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 单日期货交易统计信息
 * @author flynn
 * @date 2022/5/6 11:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayFuturesTradeCount {

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 交易笔数
     */
    private int tradeNums;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;


}
