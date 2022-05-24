package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 沉睡期
 * @author flynn
 * @date 2022/5/5 15:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepRange {

    /**
     * 开始日期 (沉睡期前的最后一次交易日期)
     */
    private Date startDate;

    /**
     * 结束日期(沉睡期后的第一次交易日期)
     */
    private Date endDate;

    /**
     * 沉睡期类型
     */
    private int type;

    public SleepRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
