package com.zzb.zhenjvan.service.impl;

import com.zzb.zhenjvan.cache.GlobalCache;
import com.zzb.zhenjvan.constant.GlobalConstant;
import com.zzb.zhenjvan.dto.UnpopularContractsCount;
import com.zzb.zhenjvan.entity.*;
import com.zzb.zhenjvan.mapper.*;
import com.zzb.zhenjvan.service.FunctionService;
import com.zzb.zhenjvan.service.RuleService;
import com.zzb.zhenjvan.util.DateUtils;
import com.zzb.zhenjvan.util.ExecutorProcessPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * name : jmh
 * time : 2022/2/26 17:37
 */
@Service
@Slf4j
public class FunctionServiceImpl implements FunctionService {

    @Resource
    AnalysisMapper analysisMapper;

    @Resource
    RuleService ruleService;

    @Resource
    private LossDetailMapper lossDetailMapper;
    @Resource
    private UnpopularContractInfoMapper unpopularContractInfoMapper;
    @Resource
    private HandingFreeAndLossMapper handingFreeAndLossMapper;
    @Resource
    private PasswordUpdateInfoMapper passwordUpdateInfoMapper;
    @Resource
    private CommissionsDetailMapper commissionsDetailMapper;
    @Resource
    private TradeRecordMapper tradeRecordMapper;
    @Resource
    private OrderRecordMapper orderRecordMapper;

    @Override
    public void run() {
        GlobalConstant.fistDate = DateUtils.strToDate("2021-11-24","yyyy-MM-dd");

        long l = System.currentTimeMillis();
        // 冷门合约
        GlobalCache.unpopularContractsCounts = ruleService.getUnpopularContractsCountList();
        // 合约层数
        GlobalCache.dateAgmtCountCntMap = ruleService.getContractsAgmtCountCntMap();

        GlobalCache.tradeDateList = analysisMapper.selectAllTradeDate();
        log.info("初始数据读取完成 耗时:{}",System.currentTimeMillis()-l);
        long startTime = System.currentTimeMillis();
//        List<String> cstPerList = new ArrayList<>();
        List<String> cstPerList = analysisMapper.selectAllCstPers();
        List<String> cstUnitList = analysisMapper.selectAllCstUnit();
//        List<String> cstUnitList = new ArrayList<>();
//        cstPerList.add("80200310");
        LinkedBlockingQueue<String> accountQueue = new LinkedBlockingQueue<>(cstPerList.size()+cstUnitList.size());
        accountQueue.addAll(cstPerList);
        accountQueue.addAll(cstUnitList);
        GlobalCache.accountQueue = accountQueue;
//        CountDownLatch latch = new CountDownLatch(16);
        CountDownLatch latch = new CountDownLatch(8);
        for (int i = 0; i < 8; i++) {
            ExecutorProcessPool.getInstance().execute(()->{
                this.startAnalysis(accountQueue,latch);
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("CountDownLatch await异常", e);
        }
        long millis = System.currentTimeMillis();
        log.info("基础规则处理完成 开始处理特殊规则 耗时:{}", millis -startTime);
//        try{
//            ruleService.ruleSS006();
//        }catch (Exception e){
//            log.error("006 出错:",e);
//        }
//        try {
//            ruleService.ruleSS003_2();
//        }catch (Exception e){
//            log.error("003 出错:",e);
//        }
//        try {
//            ruleService.ruleSS012_2();
//        }catch (Exception e){
//            log.error("012 出错:",e);
//        }
        log.info("全部处理完成 耗时:{}",System.currentTimeMillis()-startTime);
    }

    /**
     * 单客户分析
     * @param accNo
     */
    @Override
    public void analyseByCustomer(String accNo) {
        // IPMAC
//        ruleService.getIPMACWhiteList();
        // 冷门合约
//        GlobalCache.unpopularContractsCounts = ruleService.getUnpopularContractsCountList();
        // 合约层数
        GlobalCache.dateAgmtCountCntMap = ruleService.getContractsAgmtCountCntMap();
        GlobalCache.tradeDateList = analysisMapper.selectAllTradeDate();
        log.info("{} 开始",accNo);
        long startTime = System.currentTimeMillis();
        AnalysisDto analysisDto = null;
        try {
            analysisDto = this.getAnalysisData(accNo);
        }catch (Exception e){
            log.error("{} 读取出错",accNo);
            log.error("错误信息:",e);
        }
        long endReadTime = System.currentTimeMillis();
        if(analysisDto == null){
            log.info("{} 结束",accNo);
            return;
        }
        //结果储存
        ResultDto result = ResultDto.builder()
                .selfAccNo(accNo)
                .build();
        try{
            //规则1
//            ruleService.rule1207_2(analysisDto,result);
            //规则1208
//            ruleService.rule1208(analysisDto,result);
            ruleService.rule1209(analysisDto,result);
//            ruleService.ruleSS002_2(analysisDto,result);
//
//            //SS004:洗钱风险等级为高或黑名单的客户短期内发生大额资金收付或大量交易
//            ruleService.ruleSS004(analysisDto,result);
//            ruleService.ruleSS006();
//            ruleService.ruleSS003_2();
//            ruleService.ruleSS007(analysisDto,result);
//            ruleService.ruleSS008(analysisDto,result);
//            ruleService.ruleSS009(analysisDto,result);
            // ss10
//            ruleService.ruleSS010(analysisDto,result);
            // ss11
//            ruleService.ruleSS011(analysisDto,result);
//
//            ruleService.ruleSS012_2();
//            ruleService.ruleSS015(analysisDto,result);
//            ruleService.ruleSS017(analysisDto,result);
//            ruleService.ruleSS020(analysisDto,result);
//            ruleService.ruleSS029(analysisDto,result);
        }catch (Exception e){
            log.error("{} 分析出错",accNo);
            log.error("错误信息:",e);
        }
        if (result.isSaveBoolean()){
            analysisMapper.insetResult(result);
        }
        log.info("{} 结束 读取耗时:{} 总耗时:{} ",accNo,endReadTime-startTime,System.currentTimeMillis()-startTime);
    }

    @Override
    public void debug() {
        ruleService.computeRiskDate();
    }


    private void startAnalysis(LinkedBlockingQueue<String> queue, CountDownLatch latch){
        while (true){
            String accNo = queue.poll();
            if(accNo == null){
                latch.countDown();
                break;
            }
            log.info("{} 开始",accNo);
            long startTime = System.currentTimeMillis();
            AnalysisDto analysisDto = null;
            try {
                analysisDto = this.getAnalysisData(accNo);
            }catch (Exception e){
                log.error("{} 读取出错",accNo);
                log.error("错误信息:",e);
            }
            long endReadTime = System.currentTimeMillis();
            if(analysisDto == null){
                log.info("{} 结束",accNo);
                continue;
            }
            //结果储存
            ResultDto result = ResultDto.builder()
                    .selfAccNo(accNo)
                    .build();
            try{
//                ruleService.rule1207_2(analysisDto,result);
//                ruleService.rule1208(analysisDto,result);
                  ruleService.rule1209(analysisDto,result);
//                ruleService.ruleSS002_2(analysisDto,result);
//                ruleService.ruleSS004(analysisDto,result);
//                ruleService.ruleSS007(analysisDto,result);
//                ruleService.ruleSS008(analysisDto,result);
//                ruleService.ruleSS009(analysisDto,result);
//                ruleService.ruleSS010(analysisDto,result);
//                ruleService.ruleSS011(analysisDto,result);
//                ruleService.ruleSS015(analysisDto,result);
//                ruleService.ruleSS017(analysisDto,result);
//                ruleService.ruleSS020(analysisDto,result);
//                ruleService.ruleSS029(analysisDto,result);
            }catch (Exception e){
                log.error("{} 分析出错",accNo);
                log.error("错误信息:",e);
            }
            if (result.isSaveBoolean()){
                analysisMapper.insetResult(result);
            }
            log.info("{} 结束 读取耗时:{} 总耗时:{} ",accNo,endReadTime-startTime,System.currentTimeMillis()-startTime);
        }
    }


    private AnalysisDto getAnalysisData(String selfAccNo){
        //账户信息
//        TbAcc acc = analysisMapper.selectTbAccByAccNo(selfAccNo);
        //此客户日终资金汇
        List<TbFundSum> fundSumList= analysisMapper.selectFundSumbByAccNo(selfAccNo);
        if(fundSumList == null){
            fundSumList = new ArrayList<>();
        }
        //银行流水
        List<TbBankTxn> bankTxnList = analysisMapper.selectBankTxnByAccNo(selfAccNo);
        //期货委托成功流水
        List<TbFaturesTrade> fatureTradeList= analysisMapper.selectFaturesTrade(selfAccNo);
        if(fatureTradeList == null){
            fatureTradeList = new ArrayList<>();
        }
        fatureTradeList.sort(Comparator.comparing(TbFaturesTrade::getDateTime));
        //存量客户当前风险等级划分表
        TbRiskNew tbRiskNew = analysisMapper.selectTbRiskNew(selfAccNo);
        //法人相关信息
        LegalPersonInfo legalPersonInfo = analysisMapper.selectLegalPersonInfo(selfAccNo);
        //期货委托
//        List<TbFuturesEntrust> futuresEntrustList= analysisMapper.selectFuturesEntrust(selfAccNo);
        // 每日亏损记录
        List<LossDetail> lossDetailList = lossDetailMapper.selectByAccNo(selfAccNo);
        // 合约交易记录
        List<UnpopularContractInfo> unpopularContractInfoList = unpopularContractInfoMapper.selectByAccNo(selfAccNo);
        // 手续费
        List<HandingFreeAndLoss> handingFreeAndLossList = handingFreeAndLossMapper.selectByAccNo(selfAccNo);
        // 密码修改记录
        List<PasswordUpdateInfo> passwordUpdateInfoList = passwordUpdateInfoMapper.selectByAccNo(selfAccNo);
        // 佣金明细
        List<CommissionsDetail> commissionsDetailList = commissionsDetailMapper.selectByAccNo(selfAccNo);
        // 成交记录
        List<TradeRecord> tradeRecordList = tradeRecordMapper.selectByAcc(selfAccNo);
        // 报单记录
        List<OrderRecord> orderRecordList = new ArrayList<>();
        if(!selfAccNo.equals("802080888")){
            orderRecordList = orderRecordMapper.selectByAcc(selfAccNo);
        }

        AnalysisDto build = AnalysisDto.builder()
                .selfAccNo(selfAccNo)
                .fundSumList(fundSumList)
                .bankTxnList(bankTxnList)
                .fatureTradeList(fatureTradeList)
                .lossDetailList(lossDetailList)
                .legalPersonInfo(legalPersonInfo)
                .unpopularContractInfoList(unpopularContractInfoList)
                .handingFreeAndLossList(handingFreeAndLossList)
                .passwordUpdateInfoList(passwordUpdateInfoList)
                .commissionsDetailList(commissionsDetailList)
                .tradeRecordList(tradeRecordList)
                .orderRecordList(orderRecordList)
//                .futuresEntrustList(futuresEntrustList)
//                .acc(acc)
                .build();
        if (tbRiskNew != null ){
            build.setHighRisk("10".equals(tbRiskNew.getRiskCode()));
        }
        //存入客户信息
        TbCstPers cstPers = analysisMapper.selectTbCstPers(selfAccNo);
        if(cstPers != null){
            build.setCstPers(cstPers);
            build.setCustomerType(GlobalConstant.ACC_TYPE_PERSON);
        }else {
            TbCstUnit cstUnit = analysisMapper.selectTbCstUnit(selfAccNo);
            if(cstUnit != null){
                build.setCstUnit(cstUnit);
                build.setCustomerType(GlobalConstant.ACC_TYPE_COMPANY);
            }else {
                log.error("客户:{} 无客户信息",selfAccNo);
                return null;
            }
        }
        return build;
    }

}
