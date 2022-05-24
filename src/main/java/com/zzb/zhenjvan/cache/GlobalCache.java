package com.zzb.zhenjvan.cache;

import com.zzb.zhenjvan.dto.UnpopularContractsCount;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author flynn
 * @date 2022/5/6 9:38
 */
public class GlobalCache {

    /**
     * 账号队列
     */
    public static LinkedBlockingQueue<String> accountQueue = null;

    /**
     * 交易日
     */
    public static List<Date> tradeDateList = new ArrayList<Date>();

    /**
     * 冷门合约
     */
    public static List<UnpopularContractsCount> unpopularContractsCounts = new ArrayList<>();

    /**
     * mac地址白名单
     */
    public static Set<String> macWitheSet = new HashSet<>();

    /**
     * 每日合约层数
     * Map<交易日期,<合约代码,合约层数>>
     */
    public static Map<Date, Map<String, BigDecimal>> dateAgmtCountCntMap = new HashMap<>();

}
