package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/13 16:43
 */
/**
    * 报单记录
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRecord {
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
    * 交易所交易员代码
    */
    private String exchangeTraderCd;

    /**
    * 本地报单编号
    */
    private String localOrderId;

    /**
    * 结算编号
    */
    private String settlementNo;

    /**
    * 会话编号
    */
    private String sessionNo;

    /**
    * 前置编号
    */
    private String frontId;

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
    * 报单引用
    */
    private String orderRef;

    /**
    * 用户代码
    */
    private String userId;

    /**
    * 报单价格条件
    */
    private String orderPriceCon;

    /**
    * 买卖标志
    */
    private String bsFlg;

    /**
    * 开平标志
    */
    private String opencloseFlg;

    /**
    * 投机套保标记
    */
    private String hedgeFlg;

    /**
    * 价格
    */
    private BigDecimal price;

    /**
    * 数量
    */
    private BigDecimal count;

    /**
    * 强平原因
    */
    private String forceCloseReason;

    /**
    * 会员代码
    */
    private String memberId;

    /**
    * 客户编码
    */
    private String custNo;

    /**
    * 合约在交易所的代码
    */
    private String exchangeAgmtCd;

    /**
    * 报单提交状态
    */
    private String orderSubmitSts;

    /**
    * 报单编号
    */
    private String orderId;

    /**
    * 报单状态
    */
    private String orderSts;

    /**
    * 报单类型
    */
    private String orderTyp;

    /**
    * 今成交数量
    */
    private BigDecimal todayBargainCnt;

    /**
    * 剩余数量
    */
    private BigDecimal remainCnt;

    /**
    * 委托时间
    */
    private String submitTime;

    /**
    * 报单日期
    */
    private String orderDt;

    /**
    * 激活时间
    */
    private String activationTime;

    /**
    * 挂起时间
    */
    private String suspendTime;

    /**
    * 最后修改时间
    */
    private String lastModifyTime;

    /**
    * 撤消时间
    */
    private String repealDt;

    /**
    * 最后修改交易所交易员代码
    */
    private String lasttraderId;

    /**
    * 序号
    */
    private BigDecimal seqNo;

    /**
    * 用户强平标志
    */
    private String userforcecloseFlg;

    /**
    * 操作用户代码
    */
    private String operatorId;

    /**
    * 经纪公司报单编号
    */
    private String brokercorpOrderId;

    /**
    * 相关报单
    */
    private String relativeorderId;

    private String userproduct;

    /**
    * 有效期类型
    */
    private String timeCondition;

    private String ip;

    private String mac;

    private String statusMsg;
}