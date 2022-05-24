package com.zzb.zhenjvan.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * name : jmh
 * time : 2022/2/26 18:06
 * @author snjmh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AnalysisDto {

    /**
     * 是否是高风险
     */
    private boolean highRisk = false;

    /**
     * 10个人 11单位
     */
    private String customerType;

    /**
     * 账户信息
     */
    private TbAcc acc;

    /**
     * 账号
     */
    private String selfAccNo;

    /**
     * 日终资金汇总列表
     */
    private List<TbFundSum> fundSumList;

    /**
     * 资金流水
     */
    private List<TbBankTxn> bankTxnList;

    /**
     * 期货委托成功流水
     */
    private List<TbFaturesTrade> fatureTradeList;

    /**
     * 期货委托流水
     */
    private List<TbFuturesEntrust> futuresEntrustList;

    /**
     * 每日损失记录
     */
    private List<LossDetail> lossDetailList;

    /**
     * 合约交易记录
     */
    private List<UnpopularContractInfo> unpopularContractInfoList;

    /**
     * 手续费记录
     */
    private List<HandingFreeAndLoss> handingFreeAndLossList;

    /**
     * 密码修改记录
     */
    private List<PasswordUpdateInfo> passwordUpdateInfoList;

    /**
     * 佣金明细
     */
    private List<CommissionsDetail> commissionsDetailList;
    /**
     * 成交记录
     */
    private List<TradeRecord> tradeRecordList;

    /**
     * 报单记录
     */
    private List<OrderRecord> orderRecordList;

    /**
     * 自然人客户信息
     */
    private TbCstPers cstPers;

    /**
     * 非自然人客户信息
     */
    private TbCstUnit cstUnit;

    /**
     * 非自然人地址
     */
    private LegalPersonInfo legalPersonInfo;
}
