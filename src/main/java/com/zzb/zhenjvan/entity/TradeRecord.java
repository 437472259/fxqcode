package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/9 17:32
 */
/**
    * 成交记录
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecord {
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
    * 成交编号
    */
    private String dealNbr;

    /**
    * 结算编号
    */
    private String settlementNo;

    /**
    * 买卖标志
    */
    private String bsFlg;

    /**
    * 投资者代码
    */
    private String investorId;

    /**
    * 投资者帐号
    */
    private String investorAcct;

    /**
    * 合约代码
    */
    private String agmtCd;

    /**
    * 开平标志
    */
    private String opencloseFlg;

    /**
    * 用户强平标志
    */
    private String userforcecloseFlg;

    /**
    * 强平原因
    */
    private String forceCloseReason;

    /**
    * 数量
    */
    private BigDecimal count;

    /**
    * 价格
    */
    private BigDecimal price;

    /**
    * 投机套保标记
    */
    private String hedgeFlg;

    /**
    * 成交类型
    */
    private String bargainTyp;

    /**
    * 成交手数
    */
    private BigDecimal bargainCount;

    /**
    * 报单编号
    */
    private String orderId;

    /**
    * 交易所交易员代码
    */
    private String exchangeTraderCd;

    /**
    * 成交时期
    */
    private String bargainDt;

    /**
    * 成交时间
    */
    private String bargainTime;

    /**
    * 用户代码
    */
    private String userId;

    /**
    * 会员代码
    */
    private String memberId;

    /**
    * 成交价来源
    */
    private String bargainpriceSrc;

    /**
    * 本地报单编号
    */
    private String localOrderId;

    /**
    * 序号
    */
    private BigDecimal seqNo;

    /**
    * 手续费收取方式
    */
    private String feeChargeCd;

    /**
    * 手续费
    */
    private BigDecimal feeAmt;

    /**
    * 交易所手续费收取标志
    */
    private String exchangeFeeFlg;

    /**
    * 交易所手续费
    */
    private BigDecimal exchangeFeeAmt;

    /**
    * 客户编码
    */
    private String custNo;

    /**
    * 终端代码
    */
    private String userproduct;

    private String traderId;

    /**
    * 名义营业部
    */
    private String origDepartmentId;

    /**
    * 部门
    */
    private String departmentId;
}