package com.zzb.zhenjvan.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/9 19:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppTradeRecord {
    private Integer id;

    /**
    * 交易日期
    */
    private Date txDt;

    private String loginSeq;

    private String investorId;

    private String loginTime;

    private String ip;

    private String mac;

    private String localOrderId;

    private String corpOrderId;

    private String orderId;

    private String exchangeId;

    private String agmtCd;

    private String orderCnt;

    private String bargainAmt;

    private String bargainCount;

    private String dridentityid;

    private String userproduct;

    private String srcIp;

    private String srcMac;

    private String frontId;

    private String sessionId;

    private String appId;

    private String userId;
}