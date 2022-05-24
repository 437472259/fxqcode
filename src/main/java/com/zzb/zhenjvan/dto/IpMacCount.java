package com.zzb.zhenjvan.dto;

import com.zzb.zhenjvan.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * name : jmh
 * time : 2022/5/6 15:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpMacCount {

    /**
     * 统计日期
     */
    private Date today;

    /**
     * ip地址
     */
    private String ip;

    /**
     * mac地址
     */
    private String mac;

    /**
     * 符合条件账号
     */
    private String accNoList;

    /**
     * 客户数
     */
    private Integer cusNum;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 合约编号
     */
    private String contractCode;


    @Override
    public String toString(){
        return DateUtils.formatDate(this.today,"")+"-"+this.ip+"-"+this.mac;
    }
}
