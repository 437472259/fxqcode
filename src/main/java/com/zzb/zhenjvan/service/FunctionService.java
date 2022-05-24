package com.zzb.zhenjvan.service;

import javax.xml.crypto.Data;

/**
 * name : jmh
 * time : 2022/2/26 17:37
 */
public interface FunctionService {
    /**
     * 开始分析
     */
    void run();


    /**
     * 单客户分析
     * @param customer
     */
    void analyseByCustomer(String customer);

    void debug();
}
