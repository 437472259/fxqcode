package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * name : jmh
 * time : 2022/5/9 15:47
 * @author snjmh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGroupCount {

    /**
     * 客户号
     */
    private String accNo;

    /**
     * 交易金额
     */
    private BigDecimal money;
}
