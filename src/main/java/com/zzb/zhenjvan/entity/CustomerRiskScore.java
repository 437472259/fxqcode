package com.zzb.zhenjvan.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
/**
    * 客户风险评级得分
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRiskScore {
    private Integer id;

    /**
    * 日期
    */
    private Date gradeDt;

    /**
    * 账号
    */
    private String investorId;

    /**
    * 类型
    */
    private String gradeTyp;

    /**
    * 得分
    */
    private Integer score;
}