package com.zzb.zhenjvan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/7 13:50
 */
/**
    * 法人相关信息
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegalPersonInfo {
    private Integer id;

    /**
    * 账号
    */
    private String investorId;

    /**
    * 法人联系电话
    */
    private String tel;

    /**
    * 法人手机号
    */
    private String phone;

    /**
    * 法人联系地址
    */
    private String address;
}
