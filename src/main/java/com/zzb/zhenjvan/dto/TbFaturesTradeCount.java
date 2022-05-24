package com.zzb.zhenjvan.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 同一合约每日交易统计
 * name : jmh
 * time : 2022/5/7 15:56
 * @author snjmh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbFaturesTradeCount {

    /**
     * 统计日期
     */
    private Date today;

    /**
     * 合约代码
     */
    private String contractCode;

    /**
     * 买卖方向(11 买，12 卖)
     */
    private String entrustBs;

    /**
     * 交易数量
     */
    private Integer num;

    /**
     * 收数量
     */
    private Integer fundNum;

    /**
     * 付数量
     */
    private Integer payNum;


    /**
     * 交易金额
     */
    private BigDecimal money;
}
