package com.zzb.zhenjvan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * SS006保存使用
 * @author flynn
 * @date 2022/5/11 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveDto {

    /**
     * 交易日期
     */
    private Date date;

    /**
     * 客户账户
     */
    private String accNo;

    /**
     * ipMac
     */
    private String ipAndMac;

    /**
     * 合约代码
     */
    private Set<String> contractCodeSet = new HashSet<>();

    /**
     * 合约代码字符串拼接
     */
    private String contractCodeStr;

    public void addToCodeList(String code){
        this.contractCodeSet.add(code);
    }

    public SaveDto(Date date, String accNo,String ipAndMac) {
        this.date = date;
        this.accNo = accNo;
        this.ipAndMac = ipAndMac;
        this.contractCodeSet = new HashSet<>();
    }
}
