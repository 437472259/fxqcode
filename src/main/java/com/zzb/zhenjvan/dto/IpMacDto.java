package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author flynn
 * @date 2022/5/7 18:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpMacDto {

    /**
     * 交易日期
     */
    private Date tradeDate;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * mac地址
     */
    private String macAddress;
}
