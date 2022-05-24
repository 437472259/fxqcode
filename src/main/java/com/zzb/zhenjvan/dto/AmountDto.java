package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author flynn
 * @date 2022/5/14 13:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountDto {

    private BigDecimal receiveAmount = BigDecimal.ZERO;

    private BigDecimal payAmount = BigDecimal.ZERO;
}
