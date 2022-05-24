package com.zzb.zhenjvan.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Date;

/**
 * name : jmh
 * time : 2022/2/26 18:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResultDto {
    /**
     * 资金账号
     */
    @Column(value = "Self_acc_no" )
    private String selfAccNo;

    /**
     * 是否存储
     */
    private boolean saveBoolean = false;


    //rule1207
    private String rule1207 ;

    //规则1208
    private String rule1208;
    private String rule1209;


    private String ruleSS002;

    private String ruleSS003;

    private String ruleSS004;

    private String ruleSS006;

    private String ruleSS007;
    private String ruleSS008;

    private String ruleSS009;

    private String ruleSS010;

    private String ruleSS011;

    private String ruleSS015;


    private String ruleSS012;

    private String ruleSS017;

    private String ruleSS020;

    private String ruleSS029;
}
