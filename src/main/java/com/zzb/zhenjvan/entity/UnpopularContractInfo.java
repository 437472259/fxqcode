package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/7 16:19
 */

/**
 * 冷门合约开仓及平仓
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnpopularContractInfo {
    private Integer id;

    /**
     * 交易日期
     */
    private Date txDt;

    /**
     * 账号
     */
    private String investorId;

    /**
     * 失效日期
     */
    private Date invalidDate;

    /**
     * 合约名称
     */
    private String contractName;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品总成交量
     */
    private Long productTradeNums;

    /**
     * 产品总持仓量
     */
    private Long productPositionNums;

    /**
     * 单个合约成交量
     */
    private Long tradeNums;

    /**
     * 单个合约持仓量
     */
    private Long positionNums;

    /**
     * 开仓金额
     */
    private BigDecimal openAmount;

    /**
     * 平仓金额
     */
    private BigDecimal closeAmount;
}