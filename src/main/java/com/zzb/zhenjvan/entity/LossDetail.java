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
    * 每日损失记录表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LossDetail {
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
    * 每日损失
    */
    private BigDecimal lossAmount;
}