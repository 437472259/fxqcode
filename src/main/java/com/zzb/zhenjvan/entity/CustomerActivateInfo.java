package com.zzb.zhenjvan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
/**
    * 客户休眠后激活时间记录
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerActivateInfo {
    private Integer id;

    /**
    * 账号
    */
    private String investorId;

    /**
    * 激活时间 为NULL则表示没有休眠过
    */
    private String activeDt;
}