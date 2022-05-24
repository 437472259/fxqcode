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
    * 密码修改记录表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateInfo {
    private Integer id;

    /**
    * 日期
    */
    private Date tradingday;

    /**
    * 账号
    */
    private String userid;

    /**
    * 当日修改次数
    */
    private Integer num;
}