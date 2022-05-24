package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/8 18:55
 */
/**
    * 佣金明细表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionsDetail {
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
    * 合约代码
    */
    private String agmtCd;

    /**
    * 佣金金额
    */
    private BigDecimal returnAmt;
}