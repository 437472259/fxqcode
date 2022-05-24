package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 日交易统计
 * @author flynn
 * @date 2022/5/6 11:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayTradeCount {

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 收方金额
     */
    private BigDecimal receiveAmount;

    /**
     * 付方金额
     */
    private BigDecimal payAmount;
}
