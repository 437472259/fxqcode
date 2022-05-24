package com.zzb.zhenjvan.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/15 16:52
 */
/**
    * 持仓明细
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDetails {
    private Integer id;

    /**
    * 交易日期
    */
    private Date txDt;

    /**
    * 交易所代码
    */
    private String exchangeId;

    /**
    * 合约代码
    */
    private String agmtCd;

    /**
    * 投资者代码
    */
    private String investorId;

    /**
    * 投资者帐号
    */
    private String investorAcct;

    /**
    * 投机套保标记
    */
    private String hedgeFlg;

    /**
    * 买卖标志
    */
    private String bsFlg;

    /**
    * 开仓日期
    */
    private Date openDt;

    /**
    * 开仓成交编号
    */
    private String openBarginNo;

    /**
    * 结算编号
    */
    private String settlementNo;

    /**
    * 成交类型
    */
    private String bargainType;

    /**
    * 数量
    */
    private Double count;

    /**
    * 开仓价
    */
    private Double openPrice;

    /**
    * 持仓盈亏_逐日盯市
    */
    private Double hplByday;

    /**
    * 持仓盈亏_逐笔对冲
    */
    private Double hplAtatime;

    /**
    * 客户编码
    */
    private String custNo;

    /**
    * 结算价
    */
    private Double settlementPrice;

    /**
    * 昨结算价
    */
    private Double preSettlementPrice;

    /**
    * 手续费
    */
    private Double feeAmt;

    /**
    * 交易所手续费
    */
    private Double exchangeFeeAmt;

    private Double investorMarginAmt;

    /**
    * 交易所交易保证金
    */
    private Double exchangeMarginAmt;

    private String agmtcountCnt;
}