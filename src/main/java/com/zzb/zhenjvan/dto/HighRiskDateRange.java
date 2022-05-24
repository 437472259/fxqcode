package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 高风险等级持续区间
 * @author flynn
 * @date 2022/5/10 13:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HighRiskDateRange {

    /**
     * 高风险开始日期(包含)
     */
    private Date startDate;

    /**
     * 高风险结束日期(不包含) 为NULL则表示后面全是高风险
     */
    private Date endDate;

}
