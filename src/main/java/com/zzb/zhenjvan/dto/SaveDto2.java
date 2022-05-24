package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SS003规则保存类
 * @author flynn
 * @date 2022/5/11 19:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveDto2 {

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 账户
     */
    private String accNo;

    /**
     * 合约代码
     */
    private String contractCode;

    /**
     * 交易时间
     */
    private String tradeTime;


}
