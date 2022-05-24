package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 012规则保存类
 * @author flynn
 * @date 2022/5/12 16:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveDto012 {
    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * 账号
     */
    private String accNo;

    /**
     * 涉及合约代码
     */
    private String codeList;

}
