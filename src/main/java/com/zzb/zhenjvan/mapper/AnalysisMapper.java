package com.zzb.zhenjvan.mapper;

import com.zzb.zhenjvan.dto.*;
import com.zzb.zhenjvan.entity.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * name : jmh
 * time : 2022/2/26 18:07
 *
 * @author snjmh
 */
public interface AnalysisMapper {
    /**
     * 此客户日终资金汇
     *
     * @param selfAccNo
     * @return
     */
    List<TbFundSum> selectFundSumbByAccNo(String selfAccNo);

    /**
     * 资金流水
     *
     * @param selfAccNo
     * @return
     */
    List<TbBankTxn> selectBankTxnByAccNo(String selfAccNo);

    /**
     * 账户信息
     *
     * @param selfAccNo
     * @return
     */
    TbAcc selectTbAccByAccNo(String selfAccNo);

    /**
     * 期货委托成功流水
     *
     * @param selfAccNo
     * @return
     */
    List<TbFaturesTrade> selectFaturesTrade(String selfAccNo);

    /**
     * 存储结果
     *
     * @param result
     */
    void insetResult(@Param("dto") ResultDto result);

    /**
     * 存量客户当前风险等级划分表
     *
     * @param selfAccNo
     * @return
     */
    TbRiskNew selectTbRiskNew(String selfAccNo);

    /**
     * 根据Ip或Mac查询交易流水
     *
     * @param type  1-IP ，2 -MAC
     * @param set
     * @param today
     * @return
     */
    List<TbBankTxn> selectBankTxnByIpOrMac(@Param("type") Integer type, @Param("set") Set<String> set, @Param("today") String today);

    /**
     * 个人客户信息
     *
     * @param selfAccNo
     * @return
     */
    TbCstPers selectTbCstPers(String selfAccNo);

    /**
     * 单位客户信息
     *
     * @param selfAccNo
     * @return
     */
    TbCstUnit selectTbCstUnit(String selfAccNo);

    /**
     * 查询所有自然人客户
     *
     * @return
     */
    List<String> selectAllCstPers();

    List<TbCstPers> selectAllCstPersInfo();

    /**
     * 查询所有非自然人客户
     *
     * @return
     */
    List<String> selectAllCstUnit();

    /**
     * 通过tb_fund_sum表查询交易日期
     *
     * @return
     */
    List<Date> selectAllTradeDate();

    /**
     * IP-MAC白名单
     *
     * @return
     */
    List<WhiteIpMac> selectAllWhiteIpMac();


    /**
     * 期货委托
     *
     * @param selfAccNo
     * @return
     */
    List<TbFuturesEntrust> selectFuturesEntrust(String selfAccNo);

    /**
     * ip_mac交易统计
     *
     * @return
     */
    List<IpMacCount> selectIpMacCount(@Param("date") Date date);

    /**
     * 账号ip——mac统计
     *
     * @param selfAccNo
     * @return
     */
    List<Map<String, String>> selectFuturesEntrustCount(String selfAccNo);

    /**
     * 法人相关信息
     *
     * @param selfAccNo
     * @return
     */
    LegalPersonInfo selectLegalPersonInfo(String selfAccNo);

    /**
     * 查找自然人：地址，电话相同的客户
     *
     * @param addressContactList
     * @return
     */
    Set<String> selectSameAddressContactList(@Param("addressContactList") Set<String> addressContactList);

    /**
     * 每日统计
     *
     * @param selfAccNo
     * @param initDate
     * @param contractCode
     * @return
     */
    TbFaturesTradeCount selectTbFaturesTradeCount(@Param("selfAccNo") String selfAccNo, @Param("initDate") Date initDate, @Param("contractCode") String contractCode);

    /**
     * 查询客户使用的mac地址
     *
     * @param selfAccNo
     * @return
     */
    List<IpMacDto> selectAllMacByAcc(String selfAccNo);

    /**
     * @param selfAccNo
     * @return
     */
    List<CustomerRiskScore> selectCustomerRiskScore(@Param("selfAccNo") String selfAccNo);

    /**
     * 查询同一时间 买卖相反其它相同的合约交易
     *
     * @param date           交易日期
     * @param currTimeStr    交易时间 分钟 HH:mm
     * @param payReceive     买卖标识
     * @param contractCode   合约代码
     * @param businessAmount 成交数量
     * @param priceType      成交价格
     * @return
     */
    List<String> selectSS003Rule(@Param("date") Date date, @Param("currTimeStr") String currTimeStr, @Param("payReceive") String payReceive, @Param("contractCode") String contractCode, @Param("businessAmount") BigDecimal businessAmount, @Param("priceType") String priceType);

    /**
     * FuturesEntrust表Date统计
     *
     * @return
     */
    List<Date> selectFuturesEntrustDate();

    /**
     * 通过账号查询结果记录
     *
     * @param acc
     * @return
     */
    ResultDto selectResultByAcc(String acc);

    /**
     * 更新结果表
     *
     * @param result
     */
    void updateResultByAcc(ResultDto result);

    Date selectHisRiskDate(String selfAccNo);

    List<TbRiskHis> selectAllRiskDate(String accNo);

    List<TbRiskHis> selectAllRiskHis();

    /**
     * 客户组统计
     *
     * @param customerList
     * @param date
     * @return
     */
    List<CustomerGroupCount> selectCustomerGroupCount(@Param("customerList") Set<String> customerList, @Param("date") Date date);

    /**
     * 查询指定日期的委托记录
     *
     * @param date
     * @return
     */
    List<TbFuturesEntrust> selectFuturesEntrustByDate(Date date);

    /**
     * 查询指定日期的成交记录
     *
     * @param date
     * @return
     */
    List<TbFaturesTrade> selectFaturesTradeByDate(Date date);

    /**
     * 006规则保存
     *
     * @param saveDtoList
     */
    void save006(@Param("saveDtoList") List<SaveDto> saveDtoList);

    void save003(@Param("saveDtoList") List<SaveDto2> saveDto2List);

    List<LegalPersonInfo> selectAllLegalPersonInfo();

    /**
     * 查询客户日终结算流水 成交金额1000W及以上的日期
     *
     * @param accNo1
     * @return
     */
    List<Date> selectFundSumbDateByAccNo(String accNo1);

    List<TbFaturesTrade> selectFaturesTradeByDateAndAcc(@Param("date") Date date, @Param("accNo1") String accNo1, @Param("accNo2") String accNo2);

    void save012(@Param("saveList") List<SaveDto012> saveList);

    List<UnpopularContractInfo> selectContractInvalidDate();

    void insertRiskCount(RiskCountDto riskCountDto);
}

