package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/10 15:14
 */

/**
 * 市场产品及旗下合约成交持仓量
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductContractDetail {
    private Integer id;

    /**
     * 交易日期
     */
    private Date txDt;

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
     * 合约层数
     */
    private BigDecimal agmtcountCnt;
}