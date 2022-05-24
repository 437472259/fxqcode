package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * 冷门合约统计类
 * @author flynn
 * @date 2022/5/10 14:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnpopularContractsCount {

    /**
     * 交易日期
     */
    private Date date;

    /**
     * 合约代码
     */
    private String contractName;

    /**
     * 成交占比
     */
    private BigDecimal tradeDivide;

    /**
     * 持仓占比
     */
    private BigDecimal positionDivide;

    /**
     * 合约层数
     */
    private BigDecimal agmtcountCnt;
}
