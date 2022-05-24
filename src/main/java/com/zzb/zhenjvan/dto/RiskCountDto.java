package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author flynn
 * @date 2022/5/16 15:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskCountDto {

    private String accNo;

    /**
     * 上次风险等级
     */
    private String lastRiskCode;

    /**
     * 上次评级时间
     */
    private Date lastRiskDate;

    /**
     * 应该评定时间
     */
    private Date shouldDate;

    /**
     * 实际评定时间
     */
    private Date realityDate;

    /**
     * 相差天数
     */
    private int diffDay;
}
