package com.zzb.zhenjvan.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
/**
    * 客户实际交易费及交易所手续费及总亏损
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandingFreeAndLoss {
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
    * 期货公司手续费
    */
    private BigDecimal handlingFee;

    /**
    * 交易所手续费
    */
    private BigDecimal handlingFee2;

    /**
    * 总亏损金额
    */
    private BigDecimal lossAmount;
}