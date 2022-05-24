package com.zzb.zhenjvan.constant;

import com.zzb.zhenjvan.entity.WhiteIpMac;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 全局常量
 * @author flynn
 * @date 2022/5/6 9:43
 */
public class GlobalConstant {


    /**
     * 自然人标识
     */
    public static final String ACC_TYPE_PERSON = "10";

    /**
     * 非自然人标识
     */
    public static final String ACC_TYPE_COMPANY = "11";



    /**
     * 结算资金流水 收 （入金） 标识
     */
    public static final String BANK_TXN_RECEIVE = "10";

    /**
     * 结算资金流水 付 （出金） 标识
     */
    public static final String BANK_TXN_PAY = "11";

    /**
     * 期货/合约 买 标识
     */
    public static final String FUTURES_RECEIVE = "11";

    /**
     * 期货/合约 卖 标识
     */
    public static final String FUTURES_PAY = "12";

    /**
     * IP——MAC白名单
     */
    public static List<WhiteIpMac> WHITE_IP_MAC = new ArrayList<>();

    /**
     * 调试使用
     */
    public static Date fistDate = null;

}
