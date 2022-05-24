package com.zzb.zhenjvan.service;

import com.zzb.zhenjvan.dto.UnpopularContractsCount;
import com.zzb.zhenjvan.entity.AnalysisDto;
import com.zzb.zhenjvan.entity.ResultDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 规则运行
 * name : jmh
 * time : 2022/2/26 18:20
 */
public interface RuleService {
    /**
     * 存量客户长期不进行或者少量进行期货交易，或者近期新开客户一直未交易，其资金账户却发生大量的资金收付。
     * @param build
     * @param result
     */
    void rule1207_2(AnalysisDto build, ResultDto result);

    /**
     * 长期不进行期货交易的客户，或者近期新开客户一直未交易，突然在短期内原因不明地频繁进行期货交易，而且资金量巨大。
     * @param build
     * @param result
     */
    void rule1208(AnalysisDto build, ResultDto result);

    /**
     * 一段时间内，客户频繁地以同一种期货合约为标的，在以一价位开仓的同时在相同或者大致相同价位、等量或者接近等量反向开仓后平仓出局，支取资金。
     * @param build
     * @param result
     */
    void rule1209(AnalysisDto build, ResultDto result);

    /**
     * 不同的账户以同一种期货合约为标的，在同一时间点，在相同的价位进行方向相同的交易，怀疑账户操纵者是同一人。
     */
    void ruleSS001();

    /**
     * SS002
     * @param build
     * @param result
     */
    void ruleSS002_2(AnalysisDto build, ResultDto result);

    /**
     * SS003
     */
    void ruleSS003_2();

    /**
     * SS004:
     * 洗钱风险等级为高或黑名单的客户短期内发生大额资金收付或大量交易
     * @param build
     * @param result
     */
    void ruleSS004(AnalysisDto build, ResultDto result);

    /**
     * 多个客户委托交易的IP地址、MAC地址相同。
     */
    void ruleSS006();

    /**
     * 一段时间内，客户通过大额频繁交易,有较大亏损，产生大量的交易佣金，居间代理人或者客户经理因此提取高额的交易佣金。
     * @param build
     * @param result
     */
    void ruleSS007(AnalysisDto build, ResultDto result);

    /**
     * 客户频繁的发生自成交交易，而且交易金额较大。
     * @param build
     * @param result
     */
    void ruleSS008(AnalysisDto build, ResultDto result);

    /**
     * 客户频繁的在冷门合约上进行大量交易
     * @param build
     * @param result
     */
    void ruleSS009(AnalysisDto build, ResultDto result);

    /**
     * 客户建立多家三方存管银行，频繁进行不同银行出入金，资金往来量较大。
     * @param build
     * @param result
     */
    void ruleSS010(AnalysisDto build, ResultDto result);

    /**
     * 机构客户交易情况与其注册资本严重不符
     * @param build
     * @param result
     */
    void ruleSS011(AnalysisDto build, ResultDto result);

    /**
     * 个人或法人客户，其联系人的手机号码或地址相同，并且账户的交易标的相同，交易方向相同或相反，交易金额较大
     * 联系电话、或手机号码或联系地址相同的2个客户（个人或单位）
     * 其账户均针对同一合约、交易方向相反发生10笔以上交易，且累计成交金额均大于等于1000万元
     */
    void ruleSS012_2();

    /**
     * 自然人客户资产与年龄或职业不符
     * customer_risk_score中SCORE大于等于4的
     * @param analysisDto
     * @param result
     */
    void ruleSS017(AnalysisDto analysisDto, ResultDto result);

    /**
     * 投资者手续费高于交易所一定标准,频繁交易,且持续亏损。
     * @param build
     * @param result
     */
    void ruleSS015(AnalysisDto build, ResultDto result);

    /**
     * 短期交易开销帐户监控，休眠账户启用后或新开户后，短期异常大量交易，资金转出或销户。
     * @param build
     * @param result
     */
    void ruleSS020(AnalysisDto build, ResultDto result);

    /**
     * 客户在一段时间内通过不同MAC地址接入交易，并且频繁修改密码，疑似配资客户。
     * @param build
     * @param result
     */
    void ruleSS029(AnalysisDto build, ResultDto result);

    /**
     * 获取冷门合约
     * @return
     */
    List<UnpopularContractsCount> getUnpopularContractsCountList();

    /**
     * 获取每日合约层数
     * @return
     */
    Map<Date, Map<String, BigDecimal>> getContractsAgmtCountCntMap();

    /**
     * 从log中解析IPMAC白名单
     * @return
     */
    Map<Date, Set<String>> getIPMACWhiteList();

    void computeRiskDate();
}
