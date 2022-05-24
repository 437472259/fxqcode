package com.zzb.zhenjvan.service.impl;

import com.zzb.zhenjvan.cache.GlobalCache;
import com.zzb.zhenjvan.constant.GlobalConstant;
import com.zzb.zhenjvan.dto.*;
import com.zzb.zhenjvan.entity.*;
import com.zzb.zhenjvan.mapper.*;
import com.zzb.zhenjvan.service.RuleService;
import com.zzb.zhenjvan.util.DateUtils;
import com.zzb.zhenjvan.util.DecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * name : jmh
 * time : 2022/2/26 18:22
 */
@Slf4j
@Service
public class RuleServiceImpl implements RuleService {


    @Resource
    AnalysisMapper analysisMapper;
    @Resource
    private CustomerActivateInfoMapper customerActivateInfoMapper;

    @Resource
    private AppTradeRecordMapper appTradeRecordMapper;
    @Resource
    private ProductContractDetailMapper productContractDetailMapper;
    @Resource
    private LogMapper logMapper;
    @Resource
    private PositionDetailsMapper positionDetailsMapper;

    /**
     * 存量客户长期不进行或者少量进行期货交易，或者近期新开客户一直未交易，其资金账户却发生大量的资金收付。
     * 条件1：开户时间超过270天及以上的客户，连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%），且在270天内累计交易金额小于等于10万元；
     * 条件2：开户时间小于等于269天的客户，开户后连续180天未发生1笔交易；
     * 条件3：满足条件1或条件2的客户，在连续3个交易日内，个人客户资金账户与银行账户收、付单边均累计发生资金交易大于等于50万元（或单位客户或产品期货户200万元）且资金账户向银行账户转账金额占银行账户向资金账户转账金额的80%。
     * 每日交易保证金占用率小于5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%）
     * 单天收付50W
     * @param build
     * @param result
     */
    @Override
    public void rule1207_2(AnalysisDto build, ResultDto result){
        Date openTime = null;
        if(build.getCstPers()!= null){
            openTime = build.getCstPers().getOpenTime();
        }else if(build.getCstUnit() !=null){
            openTime = build.getCstUnit().getOpenTime();
        }
        if(openTime == null){
            return;
        }
        // 180日后的开户日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(openTime);
        calendar.add(Calendar.DAY_OF_MONTH,181);
        Date miniTradeDate = calendar.getTime();

        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        List<Date> tradeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : build.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        tradeDateList = build.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());
        // 数据开始日期
        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 开户日期在数据开始日期之前的 默认为之前每天都有交易
        if(openTime.before(year)){
            tradeDateList.add(DateUtils.strToDate("2020-12-31","yyyy-MM-dd"));
            Collections.sort(tradeDateList);
        }
        // 银行交易日期
        List<Date> bankTradeDateList = new ArrayList<>();
        Map<Date, List<TbBankTxn>> dateMap = build.getBankTxnList().stream().collect(Collectors.groupingBy(TbBankTxn::getDate));
        for (Date date : dateMap.keySet()) {
            List<TbBankTxn> bankTxnList = dateMap.get(date);
            // 收方总金额
            BigDecimal receiveAmount = BigDecimal.ZERO;
            // 付方总金额
            BigDecimal payAmount = BigDecimal.ZERO;
            for (TbBankTxn bankTxn : bankTxnList) {
                if (bankTxn.getLendFlag().equals(GlobalConstant.BANK_TXN_RECEIVE)) {
                    receiveAmount = receiveAmount.add(bankTxn.getAmt());
                }else {
                    payAmount = payAmount.add(bankTxn.getAmt());
                }
            }
            boolean b = false;
            if(build.getCustomerType().equals(GlobalConstant.ACC_TYPE_PERSON)){
                // 自然人
                if(payAmount.doubleValue() >= 500000 && receiveAmount.doubleValue()>=500000){
                    b = true;
                }
            }else {
                //非自然人
                if(payAmount.doubleValue() >= 2000000 && receiveAmount.doubleValue()>=2000000){
                    b = true;
                }
            }
            if(!b){
                continue;
            }
            // 付方占收方比例
            BigDecimal divide = DecimalUtils.divide(payAmount, receiveAmount);
            if(divide.doubleValue()>=0.8){
                bankTradeDateList.add(date);
            }
            bankTradeDateList.add(date);
        }
        dateMap.clear();
        Collections.sort(bankTradeDateList);
        Date debug = DateUtils.strToDate("2021-06-25","yyyy-MM-dd");
        for (Date date : bankTradeDateList) {
            if(debug.equals(date)){
                int sss = 0;
            }
            if(date.before(miniTradeDate)){
                // 当前日期距离开户不到180天 跳过
                continue;
            }
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,-270);
            Date startDate = calendar.getTime();
            // 判定当天前270天是否有交易
            long count = tradeDateList.stream().filter(r -> r.after(startDate) && r.compareTo(date) <= 0).count();
            if(count>0){
                continue;
            }
            // 270天累计交易金额
            BigDecimal totalAmount = build.getFundSumList().stream().filter(r -> r.getInitDate().after(startDate) && r.getInitDate().compareTo(date) <= 0)
                    .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(totalAmount.doubleValue()>=100000){
                continue;
            }
            result.setRule1207(DateUtils.formatDate(date,""));
            result.setSaveBoolean(true);
            return;
        }
    }

    /**
     * 长期不进行期货交易的客户，或者近期新开客户一直未交易，突然在短期内原因不明地频繁进行期货交易，而且资金量巨大。
     *交易时间：
     * 条件1：开户时间超过270天及以上的客户，连续270内未发生1笔交易；
     * 条件2：开户时间小于等于269天的客户，连续180天未发生1笔易；
     * 条件3.满足条件1或条件2的客户，连续3个交易日内任意3个交易日发生交易，且累计成交金额大于等于3000万，或单日内发生大于等于5笔交易，且累计成交金额大于等于3000万；
     * 条件4：发生条件3的交易情况后，客户期末资产(期末权益)大于等于50000。
     * @param build
     * @param result
     *
     */
    @Override
    public void rule1208(AnalysisDto build, ResultDto result) {
        // 满足条件1或条件2的最早交易日期
        List<SleepRange> sleepRangeList = this.computeSleepEndDate(build);
        if (sleepRangeList == null || sleepRangeList.isEmpty()) {
            return;
        }
        for (SleepRange sleepRange : sleepRangeList) {
            if(sleepRange == null){
                return;
            }
            Date endDate = sleepRange.getEndDate();
            // 三天后的交易日期
            int index =0;
            for (int i = 0; i < GlobalCache.tradeDateList.size(); i++) {
                if(GlobalCache.tradeDateList.get(i).equals(endDate)){
                    index = i;
                    break;
                }
            }
            index +=2;
            if(index>= GlobalCache.tradeDateList.size()){
                index = GlobalCache.tradeDateList.size()-1;
            }
            Date endTradeDate = GlobalCache.tradeDateList.get(index);
            // 日终资金汇总按天分组
            Map<Date, List<TbFundSum>> fundSumDateMap = build.getFundSumList().stream().collect(Collectors.groupingBy(TbFundSum::getInitDate));
            // 连续三天内 按天分组
            Map<Date, List<TbFaturesTrade>> tradeDateMap = build.getFatureTradeList().stream().filter(r -> r.getInitDate().compareTo(endDate) >= 0 && r.getInitDate().compareTo(endTradeDate) <= 0).collect(Collectors.groupingBy(TbFaturesTrade::getInitDate));
            if(tradeDateMap.size() >0 && tradeDateMap.keySet().size() <= 3){
                // 连续三天
                // 累计金额
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (Date date : tradeDateMap.keySet()) {
                    List<TbFundSum> tbFundSums = fundSumDateMap.get(date);
                    if(tbFundSums != null){
                        BigDecimal reduce = tbFundSums.stream().map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                        totalAmount = totalAmount.add(reduce);
                    }
                }
                if(totalAmount.doubleValue()>=30000000){
                    List<Date> dateList = new ArrayList<>(tradeDateMap.keySet());
                    Collections.sort(dateList);
                    //权益
                    List<TbFundSum> tbFundSums = fundSumDateMap.get(dateList.get(dateList.size() - 1));
                    if(tbFundSums == null || tbFundSums.isEmpty()){
                        return;
                    }
                    if(tbFundSums.get(0).getCurrentBalance().doubleValue() <50000){
                        return;
                    }
                    // 匹配
                    log.warn("1208 - 客户:{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(endDate,""));
                    List<String> dateStrList = new ArrayList<>();
                    for (Date date : dateList) {
                        dateStrList.add(DateUtils.formatDate(date,""));
                    }
                    result.setSaveBoolean(true);
                    result.setRule1208(String.join(",",dateStrList));
                    return;
                }
            }
            // 单天
            for (Date date : tradeDateMap.keySet()) {
                // 笔数
                long count = tradeDateMap.get(date).stream().map(TbFaturesTrade::getEntrustNo).distinct().count();
                if(count <5){
                    continue;
                }
                // 金额
                List<TbFundSum> tbFundSums = fundSumDateMap.get(date);
                if(tbFundSums == null){
                    continue;
                }
                BigDecimal reduce = tbFundSums.stream().map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(reduce.doubleValue() >=30000000){
                    // 期末权益
                    if (tbFundSums.get(0).getCurrentBalance().doubleValue()<50000) {
                        continue;
                    }
                    // 匹配
                    log.warn("1208 - 客户:{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(endDate,""));
                    result.setRule1208(DateUtils.formatDate(endDate,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }
    }

    @Override
    public void rule1209(AnalysisDto build, ResultDto result) {
        if(build.getCstUnit() != null && "做市机构".equals(build.getCstUnit().getIndustry())){
            return;
        }
        // 持仓明细
        List<PositionDetails> positionDetails = positionDetailsMapper.selectByAcc(build.getSelfAccNo());
        // 持仓明细按日分组
        Map<Date,Set<String>> positionDetailMap = new HashMap<>();
        positionDetails.forEach(item->{
            Set<String> orDefault = positionDetailMap.getOrDefault(item.getTxDt(), new HashSet<>());
            orDefault.add(item.getOpenBarginNo());
            positionDetailMap.put(item.getTxDt(),orDefault);
        });
        // 出金金额按日统计
        Map<Date,BigDecimal> outAmountDayMap = new HashMap<>();
        for (TbBankTxn tbBankTxn : build.getBankTxnList()) {
            if(tbBankTxn.getLendFlag().equals(GlobalConstant.BANK_TXN_PAY)){
                BigDecimal orDefault = outAmountDayMap.getOrDefault(tbBankTxn.getDate(), BigDecimal.ZERO);
                orDefault = orDefault.add(tbBankTxn.getAmt());
                outAmountDayMap.put(tbBankTxn.getDate(),orDefault);
            }
        }
        // 成交表交易日期
        List<Date> dateList = build.getTradeRecordList().stream().map(TradeRecord::getTxDt).distinct().sorted().collect(Collectors.toList());
        Date debugDate = DateUtils.strToDate("2021-08-12", "yyyy-MM-dd");
        for (Date date : dateList) {
            if(date.equals(debugDate)){
                int debug = 1;
            }
            Date endTradeDate = this.getEndTradeDate(date, 10);
            List<Date> endTradeDateList = this.getEndTradeDateList(date, 10);
            // 区间内交易流水按合约分组
            Map<String, List<TradeRecord>> codeRecordMap = build.getTradeRecordList().stream().filter(r -> r.getTxDt().compareTo(date) >= 0 && r.getTxDt().compareTo(endTradeDate) <= 0).collect(Collectors.groupingBy(r -> r.getAgmtCd()));
            for (String code : codeRecordMap.keySet()) {
                // 合约层数
                BigDecimal agmtcountCnt = GlobalCache.dateAgmtCountCntMap.get(date).get(code);
                if(agmtcountCnt == null){
                    continue;
                }
                // 按日分组
                Map<Date, List<TradeRecord>> tradeDateMap = codeRecordMap.get(code).stream().collect(Collectors.groupingBy(TradeRecord::getTxDt));
                if(!tradeDateMap.containsKey(date)){
                    // 当前合约当天没有交易 跳过
                    continue;
                }
                // 当天是否存在锁仓
//                Set<String> lockedDealNbrSet = this.calculateLocked(tradeDateMap.get(date), agmtcountCnt);
//                if(lockedDealNbrSet == null || lockedDealNbrSet.isEmpty()){
//                    // 当天当前合约无锁仓 跳过
//                    continue;
//                }
                // 10日内锁仓成交编号
                Map<Date,Set<String>> dayLockedDealNbrMap = new HashMap<>();
//                if(lockedDealNbrSet!=null && lockedDealNbrSet.isEmpty()){
//                    dayLockedDealNbrMap.put(date,lockedDealNbrSet);
//                }

                List<Date> dates = new ArrayList<>(tradeDateMap.keySet());
                Collections.sort(dates);
                for (Date recordDate : dates) {
//                    if(recordDate.equals(date)){
//                        continue;
//                    }
                    Set<String> locked = this.calculateLocked(tradeDateMap.get(recordDate), agmtcountCnt);
                    if(locked != null && !locked.isEmpty()){
                        dayLockedDealNbrMap.put(recordDate,locked);
                    }
                }
                if(dayLockedDealNbrMap.size()<3){
                    // 10日内不存在3天有锁仓
                    continue;
                }
                // 10日内的平仓日期
                Set<Date> closeDate = new HashSet<>();
//                List<Date> lockedDateList = new ArrayList<>(dayLockedDealNbrMap.keySet());
                Collections.sort(endTradeDateList);
                for (int i = 0; i < endTradeDateList.size(); i++) {
                    Date lockedDate = endTradeDateList.get(i);
                    Set<String> lockedDealNbrs = new HashSet<>();
                    Set<String> strings = dayLockedDealNbrMap.get(lockedDate);
                    if(strings == null){
                        continue;
                    }
                    lockedDealNbrs.addAll(strings);
                    for (int j = i; j < endTradeDateList.size(); j++) {
                        Date date1 = endTradeDateList.get(j);
                        // 持仓成交编号
                        Set<String> set = positionDetailMap.getOrDefault(date1,new HashSet<>());
                        // 已平仓的成交记录
                        Set<String> tmp = new HashSet<>();
                        for (String dealNbr : lockedDealNbrs) {
                            if (!set.contains(dealNbr)) {
                                tmp.add(dealNbr);
                            }
                        }
                        if(tmp.isEmpty()){
                            continue;
                        }
                        // 清除当天已平仓的成交记录 防止下一天重复计算
                        lockedDealNbrs.removeAll(tmp);
                        if(lockedDealNbrs.isEmpty()) {
                            closeDate.add(date1);
                        }
                    }
                }
                if(closeDate.isEmpty()){
                    continue;
                }
                // 计算是否满足三天内出金金额大于等于50W
                List<Date> closeDateList = new ArrayList<>(closeDate);
                Collections.sort(closeDateList);
                // 取区间内最大的平仓日作为平仓日
                Date maxCloseDate = closeDateList.get(closeDateList.size() - 1);
                // 计算 是否满足 平仓日前连续三天锁仓
                if (this.calculateContinuousDate(new ArrayList<>(dayLockedDealNbrMap.keySet()),maxCloseDate) <3) {
                    continue;
                }
                // 当天
                BigDecimal amount = outAmountDayMap.getOrDefault(maxCloseDate, BigDecimal.ZERO);
                if(amount.doubleValue()>= 500000 ){
                    // 是否满足当天前间隔内 存在连续三天锁仓交易
                    log.info("date:{}",DateUtils.formatDate(date,""));
                    log.warn("1209 1- {} - {} - {}",DateUtils.formatDate(maxCloseDate,""),DateUtils.formatDate(maxCloseDate,""),code);
                    result.setRule1209(DateUtils.formatDate(maxCloseDate,"")+code);
                    result.setSaveBoolean(true);
                    return;
                }
                // 第二天
                Date tradeDate2 = this.getEndTradeDate(maxCloseDate, 2);
                BigDecimal orDefault = outAmountDayMap.getOrDefault(tradeDate2, BigDecimal.ZERO);
                amount = amount.add(orDefault);
                if(amount.doubleValue()>= 500000 ){
                    // 是否满足当天前间隔内 存在连续三天锁仓交易
                    log.info("date:{}",DateUtils.formatDate(date,""));
                    log.warn("1209 2- {} - {} - {}",DateUtils.formatDate(maxCloseDate,""),DateUtils.formatDate(tradeDate2,""),code);
                    result.setRule1209(DateUtils.formatDate(tradeDate2,"")+code);
                    result.setSaveBoolean(true);
                    return;
                }
                // 第三天
                Date tradeDate3 = this.getEndTradeDate(maxCloseDate, 3);
                amount = amount.add(outAmountDayMap.getOrDefault(tradeDate3, BigDecimal.ZERO));
                if(amount.doubleValue()>= 500000 ){
                    // 是否满足当天前间隔内 存在连续三天锁仓交易
                    log.info("date:{}",DateUtils.formatDate(date,""));
                    log.warn("1209 3- {} - {} - {}",DateUtils.formatDate(maxCloseDate,""),DateUtils.formatDate(tradeDate3,""),code);
                    result.setRule1209(DateUtils.formatDate(tradeDate3,"")+code);
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }
    }


    private int calculateContinuousDate(List<Date> dateList,Date date){
        List<Date> dateList1 = dateList.stream().filter(r -> r.before(date)).collect(Collectors.toList());
        if(dateList1.size()<3){
            return 0;
        }
        Collections.sort(dateList1);
        // 下标
        List<Integer> indexList = new ArrayList<>();
        for (Date date1 : dateList1) {
            for (int i = 0; i < GlobalCache.tradeDateList.size(); i++) {
                if(GlobalCache.tradeDateList.get(i).equals(date1)){
                    indexList.add(i);
                    break;
                }
            }
        }
        // 判定是否存在连续三个下标差值等于1
        for (int i = 0; i < indexList.size(); i++) {
            if(i+2 < indexList.size()){
                Integer i1 = indexList.get(i);
                Integer i2 = indexList.get(i+1);
                if(i2-i1 != 1){
                    continue;
                }
                Integer i3 = indexList.get(i+2);
                if(i3-i2 !=1){
                    continue;
                }
                return 3;
            }
        }
        return 0;
    }

    /**
     * 计算是否存在锁仓
     * @param tradeRecordList 单日单合约成交记录
     * @param agmtcountCnt 合约层数
     * @return 对锁的成交编号
     */
    private Set<String> calculateLocked(List<TradeRecord> tradeRecordList,BigDecimal agmtcountCnt){
        // 开仓记录 按成交时间分钟分组
        Map<String,List<TradeRecord>> openRecordMinuteMap = new HashMap<>();
        // 平仓记录按成交时间分钟分组记录成交编号
        Map<String,Set<String>> closeMinuteMap = new HashMap<>();
        for (TradeRecord tradeRecord : tradeRecordList) {
            // 成交时间分钟 因为时间格式为00:00:00所以截取前5位
            String minute = tradeRecord.getBargainTime().substring(0, 5);
            if("0".equals(tradeRecord.getOpencloseFlg())){
                // 开仓
                List<TradeRecord> orDefault = openRecordMinuteMap.getOrDefault(minute, new ArrayList<>());
                orDefault.add(tradeRecord);
                openRecordMinuteMap.put(minute,orDefault);
            }else {
                // 平仓
                Set<String> orDefault = closeMinuteMap.getOrDefault(minute, new HashSet<>());
                orDefault.add(tradeRecord.getDealNbr());
                closeMinuteMap.put(minute,orDefault);
            }
        }
        if(openRecordMinuteMap.isEmpty()){
            return null;
        }
        // 当天满足条件的对锁单成交编号
        Set<String> dealNbrSet = new HashSet<>();
        // 当天满足条件的对锁单成交金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 判断是否锁仓
        for (String minute : openRecordMinuteMap.keySet()) {
            List<TradeRecord> recordList = openRecordMinuteMap.get(minute);
            // 买入总手数
            BigDecimal receiveNum = BigDecimal.ZERO;
            // 卖出总手数
            BigDecimal payNum = BigDecimal.ZERO;
            // 买入总价
            BigDecimal receiveAmount = BigDecimal.ZERO;
            // 卖出总价
            BigDecimal payAmount = BigDecimal.ZERO;
            // 交易编号
            List<Long> dealNbrList = new ArrayList<>();
            for (TradeRecord record : recordList) {
                if(record.getDealNbr().length()>10){
                    continue;
                }
                dealNbrList.add(Long.parseLong(record.getDealNbr()));
                if(record.getBsFlg().equals("0")){
                    // 买
                    receiveNum = receiveNum.add(record.getBargainCount());
                    receiveAmount = receiveAmount.add(record.getBargainCount().multiply(record.getPrice()).multiply(agmtcountCnt));
                }else {
                    //卖
                    payNum = payNum.add(record.getBargainCount());
                    payAmount = payAmount.add(record.getBargainCount().multiply(record.getPrice()).multiply(agmtcountCnt));
                }
            }
            // 买入均价
            BigDecimal avgReceiveAmount = DecimalUtils.divide(receiveAmount, receiveNum);
            // 卖出均价
            BigDecimal avgPayAmount = DecimalUtils.divide(payAmount, payNum);
            BigDecimal divide = DecimalUtils.divide(receiveNum, payNum);
            BigDecimal divide1 = DecimalUtils.divide(payNum, receiveNum);
            BigDecimal divide2 = DecimalUtils.divide(avgReceiveAmount, avgPayAmount);
            BigDecimal divide3 = DecimalUtils.divide(avgPayAmount, avgReceiveAmount);
            if(divide.doubleValue()>=0.999
                    && divide1.doubleValue()>=0.999
                    && divide2.doubleValue()>=0.999
                    && divide3.doubleValue()>=0.999
            ){
                Collections.sort(dealNbrList);
                // 最小值
                Long mini = dealNbrList.get(0);
                // 最大值
                Long max = dealNbrList.get(dealNbrList.size() - 1);
                Set<String> set = closeMinuteMap.get(minute);
                boolean b = true;
                if(set!= null && !set.isEmpty()){
                    for (String s : set) {
                        if(Integer.parseInt(s)>=mini && Integer.parseInt(s)<=max){
                            b= false;
                        }
                    }
                }
                if(!b){
                    // 存在平仓
                    continue;
                }
                for (Long integer : dealNbrList) {
                    dealNbrSet.add(integer.toString());
                }
                totalAmount = totalAmount.add(receiveAmount);
                totalAmount = totalAmount.add(payAmount);
            }
        }

        if(totalAmount.doubleValue()>=1000000 && dealNbrSet.size()>=3 && dealNbrSet.size()<100){
            return dealNbrSet;
        }
        return null;
    }

    /**
     *
     */
    @Override
    public void ruleSS001() {

        for (Date date : GlobalCache.tradeDateList) {
            // 查询当天成交记录
            List<TbFaturesTrade> tbFaturesTradeList = analysisMapper.selectFaturesTradeByDate(date);
            // 按合约分组
            Map<String, List<TbFaturesTrade>> codeMap = tbFaturesTradeList.stream().collect(Collectors.groupingBy(TbFaturesTrade::getContractCode));
            for (String code : codeMap.keySet()) {
                // 按对手分组
                Map<String, List<TbFaturesTrade>> accNoMap = codeMap.get(code).stream().collect(Collectors.groupingBy(TbFaturesTrade::getSelfAccNo));
                // 当天同合约 交易大于10次 总金额大于等于1000W的客户账号
                List<String> accNoMapList = new ArrayList<>();


            }
        }
    }

    /**
     * SS002 客户开立的账户长期不交易或者少量进行期货交易，或者近期新开客户一直未交易，但在多个银行间进行资金划转。当天先入金，而后再出金到不同的银行，并且金额较大。 或者不同银行入金,然后集中出金到一个银行.涉及金额较大。
     * 条件1：开户时间超过270天及以上的客户，连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%），
     * 且在270天内累计金额小于等于10万元；
     * 条件2：开户时间小于等于269天的客户，交易前一直（至少连续10天）未发生1笔交易；
     * 条件3：满足条件1或条件2的客户，
     * 单日内，个人客户资金账户与银行账户收、付单边均累计发生资金交易大于等于20万元（或单位客户或产品期货客户100万元）
     * 且资金账户接收资金的银行与资金账户转出的银行至少存在1个银行不同或资金账户接收多个银行的资金且资金账户向多个银行转出资金。
     * @param build
     * @param result
     */
    @Override
    public void ruleSS002_2(AnalysisDto build, ResultDto result) {
        Date openTime = null;
        if(build.getCstPers()!= null){
            openTime = build.getCstPers().getOpenTime();
        }else if(build.getCstUnit() !=null){
            openTime = build.getCstUnit().getOpenTime();
        }
        if(openTime == null){
            return;
        }
        // 10日后的开户日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(openTime);
        calendar.add(Calendar.DAY_OF_MONTH,11);
        Date miniTradeDate = calendar.getTime();

        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        List<Date> tradeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : build.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        tradeDateList = build.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());
        // 数据开始日期
        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 开户日期在数据开始日期之前的 默认为之前每天都有交易
        if(openTime.before(year)){
            tradeDateList.add(DateUtils.strToDate("2020-12-31","yyyy-MM-dd"));
            Collections.sort(tradeDateList);
        }
        // 交易流水按日分组
        List<Date> bankTradeDateList = new ArrayList<>();
        Map<Date, List<TbBankTxn>> dateMap = build.getBankTxnList().stream().collect(Collectors.groupingBy(TbBankTxn::getDate));
        for (Date date : dateMap.keySet()) {
            List<TbBankTxn> tbBankTxns = dateMap.get(date);
            //收方金额
            BigDecimal receiveAmount = BigDecimal.ZERO;
            // 付方金额
            BigDecimal payAmount =BigDecimal.ZERO;
            // 银行个数
            Set<String> bankNameSet = new HashSet<>();
            for (TbBankTxn tbBankTxn : tbBankTxns) {
                bankNameSet.add(tbBankTxn.getBankName());
                if(tbBankTxn.getLendFlag().equals(GlobalConstant.BANK_TXN_RECEIVE)){
                    receiveAmount = receiveAmount.add(tbBankTxn.getAmt());
                }else {
                    payAmount = payAmount.add(tbBankTxn.getAmt());
                }
            }
            if(bankNameSet.size()<2){
                continue;
            }
            if(build.getCustomerType().equals(GlobalConstant.ACC_TYPE_PERSON)){
                //自然人
                if(payAmount.doubleValue()>=200000 && receiveAmount.doubleValue()>=200000){
                    bankTradeDateList.add(date);
                }
            }else {
                //非自然人
                if(payAmount.doubleValue()>=1000000 && receiveAmount.doubleValue()>=1000000){
                    bankTradeDateList.add(date);
                }
            }
        }
        dateMap.clear();
        Collections.sort(bankTradeDateList);
        // 银行交易日期
        for (Date date : bankTradeDateList) {
            if(date.before(miniTradeDate)){
                // 当前日期距离开户不到10天 跳过
                continue;
            }
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,-270);
            Date startDate = calendar.getTime();
            // 判定当天前270天是否有交易
            long count = tradeDateList.stream().filter(r -> r.after(startDate) && r.compareTo(date) <= 0).count();
            if(count>0){
                continue;
            }
            // 270天累计交易金额
            BigDecimal totalAmount = build.getFundSumList().stream().filter(r -> r.getInitDate().after(startDate) && r.getInitDate().compareTo(date) <= 0)
                    .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(totalAmount.doubleValue()>=100000){
                continue;
            }
            result.setRuleSS002(DateUtils.formatDate(date,""));
            result.setSaveBoolean(true);
            return;
        }
    }

    /**
     *   2个及以上客户（个人或单位客户或产品期货户）的账户，同1分交易同价位、方向相反的同一品种合约，且该合约满足以下情况：
     *     1.单日同品种单合约交易量占单日同品种市场总交易量的比例小于等于2%且单日同品种单合约持仓量占单日同品种市场总持仓量的比例小于等于2%；
     *     2.该合约距离到期日大于等于30天。
     */
    @Override
    public void ruleSS003_2() {
        //已确定的冷门合约
        List<UnpopularContractsCount> resultList = GlobalCache.unpopularContractsCounts;
        Map<Date, List<UnpopularContractsCount>> dateListMap = resultList.stream()
                .filter(r -> r.getPositionDivide().doubleValue() <= 0.02 && r.getTradeDivide().doubleValue() <= 0.02)
                .collect(Collectors.groupingBy(UnpopularContractsCount::getDate));
        int index = 0;
        for (Date date : dateListMap.keySet()) {
            index++;
            log.info("SS003 日期:{} 进度:{}/{}",DateUtils.formatDate(date,""),index,dateListMap.keySet().size());
            // 当天成交明细
            List<TbFaturesTrade> tbFaturesTradeList = analysisMapper.selectFaturesTradeByDate(date);
            if(tbFaturesTradeList == null || tbFaturesTradeList.isEmpty()){
                continue;
            }
            for (UnpopularContractsCount contractsCount : dateListMap.get(date)) {
                // 筛选冷门合约交易
                List<TbFaturesTrade> tbFaturesTrades = tbFaturesTradeList.stream().filter(r -> r.getContractCode().equals(contractsCount.getContractName())).collect(Collectors.toList());
                // 按照交易时间分钟数分组
                Map<String, List<TbFaturesTrade>> minuteMap = tbFaturesTrades.stream().collect(Collectors.groupingBy(r -> DateUtils.formatDate(r.getCurrTime(), "HH-mm-ss")));
                for (String minute : minuteMap.keySet()) {
                    // 查找方向相反 数量价格相同的交易
                    Map<String,SaveDto2> saveDto2Map = new HashMap<>();
                    List<TbFaturesTrade> faturesTrades = minuteMap.get(minute);
                    for (TbFaturesTrade faturesTrade : faturesTrades) {
                        List<String> accNoList = faturesTrades.stream()
                                // 数量相同
                                .filter(r -> r.getBusinessAmount().equals(faturesTrade.getBusinessAmount()))
                                // 价格相同
                                .filter(r -> r.getSpringPriceType().equals(faturesTrade.getSpringPriceType()))
                                // 买卖相反
                                .filter(r -> !r.getEntrustBs().equals(faturesTrade.getEntrustBs()))
                                .map(TbFaturesTrade::getSelfAccNo).distinct().collect(Collectors.toList());
                        accNoList.remove(faturesTrade.getSelfAccNo());
                        if(!accNoList.isEmpty()){
                            // 统计开始结束时间
                            Date receiveStartDate =null;
                            Date receiveEndDate = null;
                            Date payStartDate =null;
                            Date payEndDate = null;
                            // 收方金额
                            BigDecimal receiveAmount = BigDecimal.ZERO;
                            // 付方金额
                            BigDecimal payAmount = BigDecimal.ZERO;

                            for (TbFaturesTrade trade : tbFaturesTrades) {
                                if(trade.getSelfAccNo().equals(faturesTrade.getSelfAccNo())){
                                    // 成交金额 = 成交数量*成交价格*合约层数
                                    BigDecimal amount = trade.getBusinessAmount().multiply(trade.getSpringPriceType()).multiply(contractsCount.getAgmtcountCnt());
                                    if(trade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                        receiveAmount = receiveAmount.add(amount);
                                    }else {
                                        payAmount = payAmount.add(amount);
                                    }
                                    if(trade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                        if(receiveStartDate == null || trade.getCurrTime().before(receiveStartDate)){
                                            receiveStartDate = trade.getCurrTime();
                                        }
                                        if(receiveEndDate == null || trade.getCurrTime().after(receiveEndDate)){
                                            receiveEndDate = trade.getCurrTime();
                                        }
                                    }else {
                                        if(payStartDate == null || trade.getCurrTime().before(payStartDate)){
                                            payStartDate = trade.getCurrTime();
                                        }
                                        if(payEndDate == null || trade.getCurrTime().after(payEndDate)){
                                            payEndDate = trade.getCurrTime();
                                        }
                                    }

                                }
                            }
                            if(payStartDate == null || payEndDate == null || receiveStartDate == null || receiveEndDate == null || (receiveAmount.doubleValue() == 0 && payAmount.doubleValue()==0)){
                                continue;
                            }
                            // 净金额
                            BigDecimal oneAmount = receiveAmount.subtract(payAmount);

                            log.warn("SS003 : 日期：{}合约：{} 时间：{} 人数:{}",DateUtils.formatDate(date,""),
                                    contractsCount.getContractName(),minute,accNoList.size()+1
                            );
                            boolean b = false;
                            for (String accNo : accNoList) {
                                Date receiveStartDate2 =null;
                                Date receiveEndDate2 = null;
                                Date payStartDate2 =null;
                                Date payEndDate2 = null;
                                // 收方金额
                                BigDecimal receiveAmount2 = BigDecimal.ZERO;
                                // 付方金额
                                BigDecimal payAmount2 = BigDecimal.ZERO;
                                for (TbFaturesTrade trade : tbFaturesTrades) {
                                    if(trade.getSelfAccNo().equals(accNo)){
                                        // 成交金额 = 成交数量*成交价格*合约层数
                                        BigDecimal amount = trade.getBusinessAmount().multiply(trade.getSpringPriceType()).multiply(contractsCount.getAgmtcountCnt());
                                        if(trade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                            receiveAmount2 = receiveAmount2.add(amount);
                                        }else {
                                            payAmount2 = payAmount2.add(amount);
                                        }
                                        if(trade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                            if(receiveStartDate2 == null || trade.getCurrTime().before(receiveStartDate2)){
                                                receiveStartDate2 = trade.getCurrTime();
                                            }
                                            if(receiveEndDate2 == null || trade.getCurrTime().after(receiveEndDate2)){
                                                receiveEndDate2 = trade.getCurrTime();
                                            }
                                        }else {
                                            if(payStartDate2 == null || trade.getCurrTime().before(payStartDate2)){
                                                payStartDate2 = trade.getCurrTime();
                                            }
                                            if(payEndDate2 == null || trade.getCurrTime().after(payEndDate2)){
                                                payEndDate2 = trade.getCurrTime();
                                            }
                                        }
                                    }
                                }
                                if(payStartDate2 == null || payEndDate2 == null || receiveStartDate2 == null || receiveEndDate2 == null){
                                    continue;
                                }
                                // 净金额相反
                                BigDecimal oneAmount2 = receiveAmount2.subtract(payAmount2);
                                if(oneAmount.compareTo(oneAmount2) != 0 && oneAmount2.add(oneAmount).doubleValue() ==0){
                                    // 方向相反 金额相同
                                    if(receiveStartDate.equals(payStartDate2) && receiveEndDate.equals(payEndDate2)
                                    && payStartDate.equals(receiveStartDate2) && payEndDate.equals(receiveEndDate2)
                                    ){
                                        b =true;
                                        saveDto2Map.put(accNo,new SaveDto2(date,accNo,contractsCount.getContractName(),minute));
                                    }
                                }
                            }
                            if(b){
                                saveDto2Map.put(faturesTrade.getSelfAccNo(),new SaveDto2(date,faturesTrade.getSelfAccNo(),contractsCount.getContractName(),minute));
                            }
                        }
                    }
                    if(!saveDto2Map.isEmpty()){
                        List<SaveDto2> saveDto2List = new ArrayList<>(saveDto2Map.values());
                        analysisMapper.save003(saveDto2List);
                    }
                }
            }
        }
    }

    /**
     * SS004:
     * 洗钱风险等级为高或黑名单的客户短期内发生大额资金收付或大量交易
     * 公司高风险和黑名单个人客户在10日内资金账户与银行账户收、付单边均累计发生资金交易大于等于200万元
     * （或单位客户或期货产品户500万元）或10日内发生期货成交交易10笔
     * （已反馈给德索，待明确，很多指标频繁交易次数是按照成交对应的委托编号相同算一次）
     * 及以上且累计金额大于等于3000万。
     * //  采用交易日的风险等级
     * @param build
     * @param result
     */
    @Override
    public void ruleSS004(AnalysisDto build, ResultDto result) {
//        if(!build.isHighRisk()){
//            return;
//        }
        List<HighRiskDateRange> riskDateRange = this.getRiskDateRange(build.getSelfAccNo());
        if(riskDateRange == null ||riskDateRange.isEmpty()){
            return;
        }
        for (HighRiskDateRange dateRange : riskDateRange) {
            // 银行账户交易统计
            List<DayTradeCount> dayTradeCountList = new ArrayList<>();
            Map<Date, List<TbBankTxn>> dateListMap = build.getBankTxnList().stream().collect(Collectors.groupingBy(TbBankTxn::getDate));
            List<Date> dateList = new ArrayList<>(dateListMap.keySet());
            Collections.sort(dateList);
            for (Date date : dateList) {
                if(date.before(dateRange.getStartDate())){
                    continue;
                }
                if(dateRange.getEndDate() != null){
                    if(!date.before(dateRange.getEndDate())){
                        continue;
                    }
                }
                // 收方金额
                BigDecimal receiveAmount = dateListMap.get(date).stream().filter(r -> r.getLendFlag().equals(GlobalConstant.BANK_TXN_RECEIVE)).map(TbBankTxn::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                // 付方金额
                BigDecimal payAmount = dateListMap.get(date).stream().filter(r -> r.getLendFlag().equals(GlobalConstant.BANK_TXN_PAY)).map(TbBankTxn::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                DayTradeCount tradeCount = new DayTradeCount();
                tradeCount.setTradeDate(date);
                tradeCount.setReceiveAmount(receiveAmount);
                tradeCount.setPayAmount(payAmount);
                dayTradeCountList.add(tradeCount);
            }
            // 按交易日期升序
            dayTradeCountList.sort(Comparator.comparing(DayTradeCount::getTradeDate));
            for (int i = 0; i < dayTradeCountList.size(); i++) {
                DayTradeCount dayTradeCount = dayTradeCountList.get(i);
//            Calendar calendar = Calendar.getInstance();
                Date startDate = dayTradeCount.getTradeDate();
//            calendar.setTime(startDate);
//            calendar.add(Calendar.DAY_OF_MONTH,10);
                Date endTradeDate = this.getEndTradeDate(startDate,10);
                // 10日内交易数据
                List<DayTradeCount> collect = dayTradeCountList.stream().filter(r -> r.getTradeDate().compareTo(startDate) >= 0 && r.getTradeDate().compareTo(endTradeDate)<=0).collect(Collectors.toList());
                // 收方总金额
                BigDecimal receiveAmount = collect.stream().map(DayTradeCount::getReceiveAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                // 付方总金额
                BigDecimal payAmount = collect.stream().map(DayTradeCount::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                // 个人客户
                if(build.getCustomerType().equals(GlobalConstant.ACC_TYPE_PERSON)){
                    if (receiveAmount.doubleValue() >= 2000000 && payAmount.doubleValue() >= 2000000) {
                        log.warn("SS004 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(startDate,""));
                        result.setRuleSS004(DateUtils.formatDate(startDate,"") +"-"+DateUtils.formatDate(endTradeDate,""));
                        result.setSaveBoolean(true);
                        return;
                    }
                }else {
                    // 非个人客户
                    if (receiveAmount.doubleValue() >= 5000000 && payAmount.doubleValue() >= 5000000) {
                        log.warn("SS004 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(startDate,""));
                        result.setRuleSS004(DateUtils.formatDate(startDate,"") +"-"+DateUtils.formatDate(endTradeDate,""));
                        result.setSaveBoolean(true);
                        return;
                    }
                }

            }

            // 期货交易统计
            List<DayFuturesTradeCount> futuresTradeCountList = new ArrayList<>();
            Map<Date, List<TbFaturesTrade>> collect = build.getFatureTradeList().stream().collect(Collectors.groupingBy(TbFaturesTrade::getInitDate));
            Map<Date, List<TbFundSum>> dayMap = build.getFundSumList().stream().collect(Collectors.groupingBy(TbFundSum::getInitDate));
            List<Date> dateList1 = new ArrayList<>(collect.keySet());
            Collections.sort(dateList1);
            for (Date date : dateList1) {
                if(date.before(dateRange.getStartDate())){
                    continue;
                }
                if(dateRange.getEndDate() != null){
                    if(!date.before(dateRange.getEndDate())){
                        continue;
                    }
                }

                //  笔数
                long count = collect.get(date).stream().map(TbFaturesTrade::getEntrustNo).distinct().count();
                // 金额
                BigDecimal amount = BigDecimal.ZERO;
                List<TbFundSum> fundSums = dayMap.get(date);
                if(fundSums != null && !fundSums.isEmpty()){
                    amount = fundSums.get(0).getBusinessBalance();
                }
                DayFuturesTradeCount tradeCount = new DayFuturesTradeCount();
                tradeCount.setTradeDate(date);
                tradeCount.setTradeNums((int) count);
                tradeCount.setTradeAmount(amount);
                futuresTradeCountList.add(tradeCount);
            }
            futuresTradeCountList.sort(Comparator.comparing(DayFuturesTradeCount::getTradeDate));
            for (DayFuturesTradeCount tradeCount : futuresTradeCountList) {
                Date tradeDate = tradeCount.getTradeDate();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(tradeDate);
//            calendar.add(Calendar.DAY_OF_MONTH,10);
                Date endTradeDate = this.getEndTradeDate(tradeDate,10);
                List<DayFuturesTradeCount> tradeCounts = futuresTradeCountList.stream().filter(r -> r.getTradeDate().compareTo(tradeDate) >= 0 && r.getTradeDate().compareTo(endTradeDate)<=0).collect(Collectors.toList());
                // 总笔数
                int totalNums = tradeCounts.stream().mapToInt(DayFuturesTradeCount::getTradeNums).sum();
                // 总金额
                BigDecimal totalAmount = tradeCounts.stream().map(DayFuturesTradeCount::getTradeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(totalNums >=10 && totalAmount.doubleValue() >= 30000000){
                    log.warn("SS004 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(tradeDate,""));
                    result.setRuleSS004(DateUtils.formatDate(tradeDate,"") +"-"+DateUtils.formatDate(endTradeDate,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }



    }

    /**
     * 获取客户风险等级为高风险的持续日期
     * @param accNo
     * @return
     */
    private List<HighRiskDateRange> getRiskDateRange(String accNo){
        // 历次风险等级
        List<TbRiskHis> tbRiskHis = analysisMapper.selectAllRiskDate(accNo);
        if(tbRiskHis == null || tbRiskHis.isEmpty()){
            return null;
        }
        tbRiskHis.forEach(item->{
            item.setRiskDate(DateUtils.strToDate(item.getTime(),""));
        });
        // 排序 时间顺序
        tbRiskHis.sort(Comparator.comparing(TbRiskHis::getRiskDate));
        List<HighRiskDateRange> riskDateRanges = new ArrayList<>();
        for (int i = 0; i < tbRiskHis.size(); i++) {
            if("10".equals(tbRiskHis.get(i).getRiskCode())){
                // 高风险评定日期
                Date startDate = tbRiskHis.get(i).getRiskDate();
                // 因监测规则是取的前10天 所以高风险日期往前推10天
                Date tradeDate = this.getStartTradeDate(startDate, 10);
                // 获取高风险结束日期
                Date endDate = null;
                if(i+1 <tbRiskHis.size()){
                    for (int j = i+1; j < tbRiskHis.size(); j++) {
                        if(!"10".equals(tbRiskHis.get(j).getRiskCode())){
                            endDate = tbRiskHis.get(j).getRiskDate();
                        }
                    }
                }
                riskDateRanges.add(new HighRiskDateRange(tradeDate,endDate));
            }
        }
        return riskDateRanges;
    }


    /**
     * 同一IPMAC SRCIPSCRMAC 同一APP同一天大于100人也加入白名单
     * 委托表 按天查询
     * 统计同一合约 IP+MAC相同的客户数 大于等于5小于100
     * IP+MAC排除白名单
     * 找到的所有客户 当前合约委托金额大于等于1000w
     * 查询成交表当天记录 通过上面找到的委托编号及客户统计当天成交金额 总成交金额大于等于1000W
     * 成交金额 = 成交数量*成交价格*合约层数
     *
     */
    @Override
    public void ruleSS006(){
        // 白名单IP+MAC
        Set<String> ipMacSet = new HashSet<>();
        GlobalConstant.WHITE_IP_MAC.forEach(o -> ipMacSet.add(o.getIp()+o.getMac().toUpperCase(Locale.ROOT).replace(":","")));
        //交易日期
        List<Date> dateList= analysisMapper.selectFuturesEntrustDate();
//        List<Date> dateList = new ArrayList<>();
//        dateList.add(DateUtils.strToDate("2022-03-09","yyyy-MM-dd"));
//        dateList.add(DateUtils.strToDate("2022-02-21","yyyy-MM-dd"));
//        dateList.add(DateUtils.strToDate("2022-01-17","yyyy-MM-dd"));
        int index = 0;
        for (Date date : dateList) {
            index++;
            log.info("当前日期:{} 进度{}/{}",DateUtils.formatDate(date,""),index,dateList.size());
            // 当日委托流水
            List<TbFuturesEntrust> tbFuturesEntrusts = analysisMapper.selectFuturesEntrustByDate(date);
            // 当日成交流水
            List<TbFaturesTrade> tbFaturesTradeList = analysisMapper.selectFaturesTradeByDate(date);
            // 当日所有合约层数
            List<ProductContractDetail> contractDetailList = productContractDetailMapper.selectByDate(date);
            // 当日APP信息
            List<AppTradeRecord> appTradeRecords = appTradeRecordMapper.selectByDate(date);
            // 根据IP+MAC分组
            Map<String, List<TbFuturesEntrust>> ipMacCountMap = tbFuturesEntrusts.stream()
                    .filter(r->!r.getIpAddress().equals("@N"))
                    .filter(r->!r.getMacAddress().equals("@N"))
                    .collect(Collectors.groupingBy(r -> r.getIpAddress() + r.getMacAddress().replace(":", "").toUpperCase(Locale.ROOT)));
            tbFuturesEntrusts = null;
            for (String ipAndMac : ipMacCountMap.keySet()) {
                // 排除白名单
                if(ipMacSet.contains(ipAndMac)){
                    continue;
                }
                // 当前IP MAC当天同时一时间登录的客户 MAC可能两张表中大小写不一致 所有全部转为大写处理
                Map<String, List<AppTradeRecord>> loginTimeMap = appTradeRecords.stream().filter(r -> r.getIp().equals(ipMacCountMap.get(ipAndMac).get(0).getIpAddress()))
                        .filter(r -> r.getMac().toUpperCase(Locale.ROOT).equals(ipMacCountMap.get(ipAndMac).get(0).getMacAddress().toUpperCase(Locale.ROOT)))
                        .collect(Collectors.groupingBy(AppTradeRecord::getLoginTime));
                // 当天当前IPMAC满足同时登录的客户
                Set<String> allAccNoSet = new HashSet<>();
                for (List<AppTradeRecord> value : loginTimeMap.values()) {
                    List<String> collect = value.stream().map(AppTradeRecord::getInvestorId).distinct().collect(Collectors.toList());
                    if(collect.size()>=5){
                        allAccNoSet.addAll(collect);
                    }
                }
                // 当日结果Map<客户账户,>
                Map<String,SaveDto> resultMap = new HashMap<>();
                // 涉及人数
                Set<String> accNoSet = new HashSet<>();
                BigDecimal totalAmount = BigDecimal.ZERO;
                // 根据合约分组
                Map<String, List<TbFuturesEntrust>> contractCodeMap = ipMacCountMap.get(ipAndMac).stream().collect(Collectors.groupingBy(TbFuturesEntrust::getContractCode));
                for (String contractCode : contractCodeMap.keySet()) {
                    if(contractCode == null || contractCode.isEmpty()){
                        continue;
                    }
                    // 当前合约人数
                    List<String> accNoList = contractCodeMap.get(contractCode).stream().map(TbFuturesEntrust::getSelfAccNo).distinct().collect(Collectors.toList());
                    if(accNoList.size() >=5 && accNoList.size() <=100){
                        // 当前合约层数
                        BigDecimal amount = null;
                        for (ProductContractDetail contractDetail : contractDetailList) {
                            if(contractDetail.getContractName().equals(contractCode)){
                                amount = contractDetail.getAgmtcountCnt();
                                break;
                            }
                        }
                        if(amount == null || amount.doubleValue() == 0){
                            log.error("日期:{} 合约:{} 无合约层数",DateUtils.formatDate(date,""),contractCode);
                            continue;
                        }
                        // 排除没有同时登录的客户
                        List<String> accNoList2 = new ArrayList<>();
                        accNoList.forEach(item->{
                            if(allAccNoSet.contains(item)){
                                accNoList2.add(item);
                            }
                        });
                        if(accNoList2.size()<5){
                            continue;
                        }
                        // 委托编号
                        List<String> entrustNoList = contractCodeMap.get(contractCode).stream().map(TbFuturesEntrust::getEntrustNo).distinct().collect(Collectors.toList());
                        // 根据委托编号及accNo统计成交金额 成交金额 = 成交数量*成交价格*合约层数
                        // 筛选成交表中当前客户组客户当前合约成交记录
                        List<TbFaturesTrade> faturesTrades = tbFaturesTradeList.stream()
                                .filter(r->r.getContractCode() != null && !r.getContractCode().isEmpty())
                                .filter(r -> r.getContractCode().equals(contractCode))
                                .filter(r->entrustNoList.contains(r.getEntrustNo()))
                                .filter(r -> accNoList2.contains(r.getSelfAccNo()))
                                .collect(Collectors.toList());

                        // 按分钟分组
                        Map<Date, List<TbFaturesTrade>> mMap = faturesTrades.stream().collect(Collectors.groupingBy(TbFaturesTrade::getCurrTime));
                        for (List<TbFaturesTrade> value : mMap.values()) {
                            Set<String> set1 = new HashSet<>();
                            BigDecimal totalAmount2 = BigDecimal.ZERO;
                            for (TbFaturesTrade faturesTrade : value) {
                                BigDecimal tradeAmount = faturesTrade.getBusinessAmount().multiply(faturesTrade.getSpringPriceType()).multiply(amount);
                                totalAmount2 = totalAmount2.add(tradeAmount);
                                set1.add(faturesTrade.getSelfAccNo());
                            }
                            if(set1.size()>=5){
                                if(set1.contains("809038090")){
                                    int debug = 2;
                                }
                                totalAmount = totalAmount.add(totalAmount2);
                                accNoSet.addAll(set1);
                            }
                        }
                    }
                }
                if(accNoSet.size() >=5 && totalAmount.doubleValue()>=10000000){
                    // 满足条件
                    for (String accNo : accNoSet) {
                        SaveDto saveDto = resultMap.get(accNo);
                        if(saveDto == null){
                            saveDto = new SaveDto(date,accNo,ipAndMac);
                        }
//                        saveDto.addToCodeList(contractCode);
                        resultMap.put(accNo,saveDto);
                    }
                }
                // 保存到数据库
                if(!resultMap.isEmpty()){
                    List<SaveDto> saveDtoList = new ArrayList<>(resultMap.values());
                    saveDtoList.forEach(item->{
                        item.setContractCodeStr(String.join("|",item.getContractCodeSet()));
                    });
                    analysisMapper.save006(saveDtoList);
                }
            }
        }
    }

    /**
     * 一段时间内，客户通过大额频繁交易,有较大亏损，产生大量的交易佣金，居间代理人或者客户经理因此提取高额的交易佣金。
     * 连续30个交易日内，存在交易的天数大于等于20个交易日，
     * 累计交易笔数大于20次且累计成交金额大于等于1亿元，
     * 该30个交易日内，
     * 总亏损金额（多、空持仓及平仓盈亏总和）大于等于100万元且佣金费用大于等于10万元。
     * @param build
     * @param result
     */
    @Override
    public void ruleSS007(AnalysisDto build, ResultDto result) {
        if(build.getCommissionsDetailList()== null || build.getCommissionsDetailList().isEmpty()){
            return;
        }
        // 委托成交日期
        List<Date> tradeDateList = build.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate).distinct().sorted().collect(Collectors.toList());
        for (Date date : tradeDateList) {
            // 连续30个交易日最一天日期
            Date endTradeDate = this.getEndTradeDate(date, 30);
            // 计算期间共有多少个交易日有交易
            long count = tradeDateList.stream().filter(r -> r.compareTo(date) >= 0 && r.compareTo(endTradeDate) <= 0).count();
            if (count <20){
                continue;
            }
            // 累计成交金额
            BigDecimal reduce = build.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(date) >= 0 && r.getInitDate().compareTo(endTradeDate) <= 0)
                    .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 1亿
            BigDecimal decimal = BigDecimal.valueOf(100000000);
            if(reduce.compareTo(decimal) <0){
                continue;
            }
            // 总亏损金额
            BigDecimal lossAmount = build.getLossDetailList().stream().filter(r -> r.getTxDt().compareTo(date) >= 0 && r.getTxDt().compareTo(endTradeDate) <= 0)
                    .map(LossDetail::getLossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            //
            if(lossAmount.doubleValue() >-1000000){
                continue;
            }
            // 佣金费用
            //  计算佣金
            BigDecimal reduce1 = build.getCommissionsDetailList().stream().filter(r -> r.getTxDt().compareTo(date) >= 0 && r.getTxDt().compareTo(endTradeDate) <= 0)
                    .map(CommissionsDetail::getReturnAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
//            BigDecimal reduce1 = build.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(date) >= 0 && r.getInitDate().compareTo(endTradeDate) <= 0)
//                    .map(TbFundSum::getFrozenOpenFare).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (reduce1.doubleValue()>=100000) {
                log.warn("SS007 - 客户：{} 日期：{} - {}",build.getSelfAccNo(),DateUtils.formatDate(date,""),DateUtils.formatDate(endTradeDate,""));
                result.setRuleSS007(DateUtils.formatDate(date,""));
                result.setSaveBoolean(true);
                return;
            }
        }
    }

    /**
     * trade_record成交表
     * 客户同一合约，同一成交时间 买卖相反 交易编号相同 郑商所相差1
     * @param build
     * @param result
     */
    @Override
    public void ruleSS008(AnalysisDto build, ResultDto result) {
        // 需要排除的 order_id <交易日期,需要排除的OrdeId>
        Map<Date,Set<String>> excludeOrderIdMap = new HashMap<>();
        for (OrderRecord orderRecord : build.getOrderRecordList()) {
            if((orderRecord.getOrderPriceCon().equals("2") && orderRecord.getTimeCondition().equals("1")) || orderRecord.getAgmtCd().contains("&")){
                Set<String> aDefault = excludeOrderIdMap.getOrDefault(orderRecord.getTxDt(), new HashSet<>());
                aDefault.add(orderRecord.getOrderId());
                excludeOrderIdMap.put(orderRecord.getTxDt(),aDefault);
            }
        }
        //
        String zss = "CZCE";
        String zjs = "CFFEX";
        // 郑商所 成交数据
        List<TradeRecord> zsTradeRecordList = new ArrayList<>();
        // 其它交易所成交数据
        List<TradeRecord> otherTradeRecordList = new ArrayList<>();
        for (TradeRecord tradeRecord : build.getTradeRecordList()) {
            if (tradeRecord.getExchangeId().equals(zss) && !tradeRecord.getHedgeFlg().equals("3")) {
                zsTradeRecordList.add(tradeRecord);
            }else {
                if(tradeRecord.getExchangeId().equals(zjs) && !tradeRecord.getBargainTime().equals("20:59:00") && !tradeRecord.getBargainTime().equals("08:59:00")){
                    if(tradeRecord.getHedgeFlg().equals("1")){
                        otherTradeRecordList.add(tradeRecord);
                    }
                }else {
                    if(!tradeRecord.getHedgeFlg().equals("3")){
                        otherTradeRecordList.add(tradeRecord);
                    }
                }
            }
        }
        Date debug = DateUtils.strToDate("2021-05-10","yyyy-MM-dd");
        if(!otherTradeRecordList.isEmpty()){
            // 按天分组
            Map<Date, List<TradeRecord>> dateListMap = otherTradeRecordList.stream().collect(Collectors.groupingBy(TradeRecord::getTxDt));
            for (Date date : dateListMap.keySet()) {
                // 需要排除的orderId
                Set<String> excludeOrderIdSet =  excludeOrderIdMap.getOrDefault(date,new HashSet<>());
                // 按合约分组
                Map<String, List<TradeRecord>> codeMap = dateListMap.get(date).stream()
                        .filter(r->!excludeOrderIdSet.contains(r.getOrderId()))
                        .collect(Collectors.groupingBy(TradeRecord::getAgmtCd));
                // 当天合约层数
                Map<String, BigDecimal> decimalMap = GlobalCache.dateAgmtCountCntMap.get(date);
                if(decimalMap == null){
                    log.error("SS008 日期：{} 没有合约层数记录",DateUtils.formatDate(date,""));
                    continue;
                }
                // 已记录的自成交编号
                Set<String> useDealNbrSet = new HashSet<>();
                // 当天当前合约自成交总金额
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (String code : codeMap.keySet()) {
                    // 当前合约层数
                    BigDecimal agmtCountCnt = decimalMap.get(code);
                    if(agmtCountCnt == null){
                        log.error("SS008 日期：{} 合约代码:{} 没有合约层数记录",DateUtils.formatDate(date,""),code);
                        continue;
                    }

                    // 按时间分组及成交编号分组
                    Map<String, List<TradeRecord>> timeMap = codeMap.get(code).stream().collect(Collectors.groupingBy(r -> r.getBargainTime() + r.getDealNbr()));
                    for (String timeAndDealNbr : timeMap.keySet()) {
                        List<TradeRecord> tradeRecords = timeMap.get(timeAndDealNbr);
                        if(useDealNbrSet.contains(tradeRecords.get(0).getDealNbr())){
                            continue;
                        }
                        for (TradeRecord tradeRecord : tradeRecords) {
                            // 找方向相反 金额/手数相同
                            List<TradeRecord> collect = tradeRecords.stream().filter(r -> r.getPrice().equals(tradeRecord.getPrice()))
                                    .filter(r -> r.getBargainCount().equals(tradeRecord.getBargainCount()))
                                    .filter(r -> !r.getBsFlg().equals(tradeRecord.getBsFlg()))
                                    .collect(Collectors.toList());
                            if(collect.size() == 1){
                                useDealNbrSet.add(tradeRecord.getDealNbr());
                                useDealNbrSet.add(collect.get(0).getDealNbr());
                                totalAmount = totalAmount.add(collect.get(0).getPrice().multiply(collect.get(0).getBargainCount()).multiply(agmtCountCnt));
                                break;
                            }else if(collect.size() >1){
                                log.warn("日期：{} 成交编号: {} 有多个记录",DateUtils.formatDate(date,""),tradeRecord.getDealNbr());
                            }
                        }
                    }
                }
                if(useDealNbrSet.size() >=5 && totalAmount.doubleValue() >= 10000){
                    log.info("SS008 {} - {}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                    result.setRuleSS008(DateUtils.formatDate(date,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }
        otherTradeRecordList.clear();
        if (!zsTradeRecordList.isEmpty()) {
            // 按天分组
            Map<Date, List<TradeRecord>> dateListMap = zsTradeRecordList.stream().collect(Collectors.groupingBy(TradeRecord::getTxDt));
            for (Date date : dateListMap.keySet()) {
                // 需要排除的orderId
                Set<String> excludeOrderIdSet =  excludeOrderIdMap.getOrDefault(date,new HashSet<>());
                // 按合约分组
                Map<String, List<TradeRecord>> codeMap = dateListMap.get(date).stream()
                        .filter(r->!excludeOrderIdSet.contains(r.getOrderId()))
                        .collect(Collectors.groupingBy(TradeRecord::getAgmtCd));
                // 当天合约层数
                Map<String, BigDecimal> decimalMap = GlobalCache.dateAgmtCountCntMap.get(date);
                if(decimalMap == null){
                    log.error("SS008 日期：{} 没有合约层数记录",DateUtils.formatDate(date,""));
                    continue;
                }
                // 已记录的自成交编号
                Set<String> useDealNbrSet = new HashSet<>();
                // 当天当前合约自成交总金额
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (String code : codeMap.keySet()) {
                    // 当前合约层数
                    BigDecimal agmtCountCnt = decimalMap.get(code);
                    if(agmtCountCnt == null){
                        log.error("SS008 日期：{} 合约代码:{} 没有合约层数记录",DateUtils.formatDate(date,""),code);
                        continue;
                    }
                    // 按时间分组分组
                    Map<String, List<TradeRecord>> timeMap = codeMap.get(code).stream().collect(Collectors.groupingBy(TradeRecord::getBargainTime));
                    for (String timeAndDealNbr : timeMap.keySet()) {
                        List<TradeRecord> tradeRecords = timeMap.get(timeAndDealNbr);
                        if(useDealNbrSet.contains(tradeRecords.get(0).getDealNbr())){
                            continue;
                        }
                        for (TradeRecord tradeRecord : tradeRecords) {
                            // 找方向相反 金额/手数相同
                            List<TradeRecord> collect = tradeRecords.stream().filter(r -> r.getPrice().equals(tradeRecord.getPrice()))
                                    .filter(r -> r.getBargainCount().equals(tradeRecord.getBargainCount()))
                                    .filter(r -> !r.getBsFlg().equals(tradeRecord.getBsFlg()))
                                    .collect(Collectors.toList());
                            // 成交编号差1的数据
                            List<TradeRecord> recordList = new ArrayList<>();
                            Long dealNbr = Long.parseLong(tradeRecord.getDealNbr());
                            for (TradeRecord record : collect) {
                                Long dealNbr2 = Long.parseLong(record.getDealNbr());
                                long l = dealNbr - dealNbr2;
                                if(l==1 || l==-1){
                                    recordList.add(record);
                                }
                            }
                            if(recordList.size() == 1){
                                useDealNbrSet.add(tradeRecord.getDealNbr());
                                useDealNbrSet.add(recordList.get(0).getDealNbr());
                                totalAmount = totalAmount.add(recordList.get(0).getPrice().multiply(recordList.get(0).getBargainCount()).multiply(agmtCountCnt));
                                break;
                            }else if(recordList.size() >1){
                                log.warn("日期：{} 成交编号: {} 有多个记录",DateUtils.formatDate(date,""),tradeRecord.getDealNbr());
                            }
                        }
                    }
                }
                if(useDealNbrSet.size() >=10 && totalAmount.doubleValue()>=10000){
                    log.info("SS008 {} - {}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                    result.setRuleSS008(DateUtils.formatDate(date,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }
    }

    /**
     * 单日内发生10次及以上的交易，合计金额大于等于1000万元，交易的合约符合以下情况：
     * 1.单日同品种单合约交易量占单日同品种市场总交易量的比例小于等于1%且单日同品种单合约持仓量占单日同品种市场总持仓量的比例小于等于1%；
     * 2.该合约距离到期日大于等于30天。
     * 注1：非做市商客户。
     * 注2：合约的开仓额>平仓额（净开仓）的客户
     * 频繁交易次数是统计开平方向都算的次数吗？此处是指开仓的总金额需要大于平仓总金额吗？
     * @param build
     * @param result
     */
    @Override
    public void ruleSS009(AnalysisDto build, ResultDto result) {
        if(build.getCstUnit() != null && "做市机构".equals(build.getCstUnit().getIndustry())){
            return;
        }
        //已确定的冷门合约
        List<UnpopularContractsCount> resultList = GlobalCache.unpopularContractsCounts;
//        Map<Date, List<UnpopularContractsCount>> dateListMap = resultList.stream().filter(r -> r.getTradeDivide().doubleValue() <= 0.01)
//                .collect(Collectors.groupingBy(UnpopularContractsCount::getDate));
        Map<Date, List<UnpopularContractsCount>> dateListMap = resultList.stream().filter(r -> r.getPositionDivide().doubleValue() <= 0.01 && r.getTradeDivide().doubleValue() <= 0.01)
                .collect(Collectors.groupingBy(UnpopularContractsCount::getDate));

        // 冷门合约 Map<交易日期，合约代码Set>
        Map<Date,Set<String>> resultMap = new HashMap<>();
        if(build.getUnpopularContractInfoList() == null || build.getUnpopularContractInfoList().isEmpty()){
            return;
        }
        // 合约交易日期
        List<Date> dateList = build.getUnpopularContractInfoList().stream().map(UnpopularContractInfo::getTxDt).distinct().sorted().collect(Collectors.toList());
        Date s = DateUtils.strToDate("2021-08-12","yyyy-MM-dd");
        for (Date date : dateList) {

            if(date.equals(s)){
                int debug = 0;
            }
            if(dateListMap.get(date) == null){
                // 没有冷门合约交易的天数排除
                continue;
            }
            // 当天冷门合约代码
            Set<String> contractsSet = dateListMap.get(date).stream().map(UnpopularContractsCount::getContractName).collect(Collectors.toSet());
            List<UnpopularContractInfo> contractInfoList = build.getUnpopularContractInfoList().stream().filter(r -> r.getTxDt().equals(date)).collect(Collectors.toList());
            for (UnpopularContractInfo contractInfo : contractInfoList) {
                if(!contractsSet.contains(contractInfo.getContractName())){
                    continue;
                }
                // 到期日
                int days = DateUtils.getDateDays(contractInfo.getTxDt(), contractInfo.getInvalidDate());
                if(days>=30){
                    // 冷门合约
                    Set<String> orDefault = resultMap.getOrDefault(date, new HashSet<>());
                    orDefault.add(contractInfo.getContractName());
                    resultMap.put(date,orDefault);
                }
            }
        }
        if(resultMap.isEmpty()){
            return;
        }
        // 成交记录
        List<TradeRecord> tradeRecords = build.getTradeRecordList();
        if(tradeRecords == null || tradeRecords.isEmpty()){
            return;
        }
        List<Date> resultDateList = new ArrayList<>();
        List<Date> dateList1 = new ArrayList<>(resultMap.keySet());
        Collections.sort(dateList1);
        for (Date date : dateList1) {
            if(GlobalConstant.fistDate != null && date.before(GlobalConstant.fistDate)){
                continue;
            }
            if(date.equals(s)){
                int ss = 0;
            }
            // 当天成交记录
            List<TradeRecord> tradeRecordList = tradeRecords.stream().filter(r -> r.getTxDt().equals(date)).collect(Collectors.toList());
            // 冷门合约总交易金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 冷门合约数据
            List<UnpopularContractsCount> contractsCounts = dateListMap.get(date);
            Set<String> codeSet = resultMap.get(date);
            // 最终确定符合的冷门合约
            Set<String> resultCodeSet = new HashSet<>();
            for (String code : codeSet) {
                for (UnpopularContractsCount contractsCount : contractsCounts) {
                    if(contractsCount.getContractName().equals(code)){
                        // 合约层数
                        BigDecimal agmtcountCnt = contractsCount.getAgmtcountCnt();
                        // 筛选当前合约成交记录
                        List<TradeRecord> records = tradeRecordList.stream().filter(r -> r.getAgmtCd().equals(code)).collect(Collectors.toList());
                        // 开仓金额
                        BigDecimal openAmount = BigDecimal.ZERO;
                        // 平仓金额
                        BigDecimal closeAmount = BigDecimal.ZERO;
                        for (TradeRecord record : records) {
                            // 交易金额 = 价格*手数*合约层数
                            BigDecimal tradeAmount = record.getPrice().multiply(record.getBargainCount()).multiply(agmtcountCnt);
                            if("0".equals(record.getOpencloseFlg())){
                                // 开仓
                                openAmount = openAmount.add(tradeAmount);
                            }else{
                                // 平仓
                                closeAmount = closeAmount.add(tradeAmount);
                            }
                        }
                        if(openAmount.compareTo(closeAmount)>0 && openAmount.add(closeAmount).doubleValue()>=10000000){
                            resultCodeSet.add(code);
                            totalAmount = totalAmount.add(openAmount);
                            totalAmount = totalAmount.add(closeAmount);
                        }
                    }
                }
            }
//            if(totalAmount.doubleValue()<3000000){
//                continue;
//            }
            if(totalAmount.doubleValue()<10000000){
                continue;
            }
            // 冷门合约当天交易次数
            //  当天交易次数 应从成交明细表中取成交编号字段
            long count = tradeRecords.stream().filter(r->r.getTxDt().equals(date)).filter(r->resultCodeSet.contains(r.getAgmtCd()))
                    .map(TradeRecord::getDealNbr).distinct().count();
            if(count>=10){
                resultDateList.add(date);
                log.warn("SS009 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                result.setRuleSS009(DateUtils.formatDate(date,"")+"|"+String.join("-",resultCodeSet));
                result.setSaveBoolean(true);
                return;
            }
        }
//        if(!resultDateList.isEmpty()){
//            Collections.sort(resultDateList);
//            List<String> stringList = new ArrayList<>();
//            resultDateList.forEach(item-> stringList.add(DateUtils.formatDate(item,"")));
//            result.setRuleSS009(String.join("|",stringList));
//            result.setSaveBoolean(true);
//            return;
//        }
    }

    /**
     * 客户建立多家三方存管银行，频繁进行不同银行出入金，资金往来量较大。
     * 连续10交易日内，
     * 个人客户资金账户与3家以上银行账户收、付任一单边累计发生资金交易大于等于100万元
     * （或单位客户或产品期货户200万元），
     * 且收、付任一单边单笔发生资金交易大于等于20万元
     * （或单位客户或产品期货户50万元）
     * @param build
     * @param result
     */
    @Override
    public void ruleSS010(AnalysisDto build, ResultDto result) {
        // 出金/入金交易日期
        List<Date> tradeDate = build.getBankTxnList().stream().map(TbBankTxn::getDate).distinct().sorted().collect(Collectors.toList());
        for (Date date : tradeDate) {
            if(GlobalConstant.fistDate != null && date.before(GlobalConstant.fistDate)){
                continue;
            }
            // 第10天的交易日期
            Date endTradeDate = this.getEndTradeDate(date,10);
            // 10天内交易流水
            List<TbBankTxn> tbBankTxns = build.getBankTxnList().stream().filter(r -> r.getDate().compareTo(date) >= 0 && r.getDate().compareTo(endTradeDate) <= 0).collect(Collectors.toList());
            // 10天内涉及银行数
            // 涉及银行交易金额必须大于0
            long count = tbBankTxns.stream().filter(r->r.getAmt().doubleValue()>0).map(TbBankTxn::getBankName).distinct().count();
            if(count <3){
                continue;
            }
            // 收方金额
            BigDecimal receiveAmount = tbBankTxns.stream().filter(r->r.getLendFlag().equals(GlobalConstant.BANK_TXN_RECEIVE)).map(TbBankTxn::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 付方金额
            BigDecimal payAmount = tbBankTxns.stream().filter(r->r.getLendFlag().equals(GlobalConstant.BANK_TXN_PAY)).map(TbBankTxn::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 任意收付单边单笔金额大于等于20W
            // 收方最大金额
            BigDecimal receiveMaxAmount = tbBankTxns.stream().filter(r -> r.getLendFlag().equals(GlobalConstant.BANK_TXN_RECEIVE)).map(TbBankTxn::getAmt).distinct().max(Comparator.naturalOrder()).orElse(null);
            // 付方最大金额
            BigDecimal payMaxAmount = tbBankTxns.stream().filter(r -> r.getLendFlag().equals(GlobalConstant.BANK_TXN_PAY)).map(TbBankTxn::getAmt).distinct().max(Comparator.naturalOrder()).orElse(null);
            if(build.getCustomerType().equals(GlobalConstant.ACC_TYPE_PERSON)){
                boolean b = false;
                if(payMaxAmount != null && payMaxAmount.doubleValue() >=200000){
                    b =true;
                }
                if(receiveMaxAmount != null && receiveMaxAmount.doubleValue() >=200000){
                    b =true;
                }
                if(!b){
                    continue;
                }
                // 自然人
                if(receiveAmount.doubleValue() >=1000000 || payAmount.doubleValue() >=1000000){
                    log.warn("ss10 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                    result.setRuleSS010(DateUtils.formatDate(date,"")+"/"+DateUtils.formatDate(endTradeDate,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }else {
                // 非自然人
                boolean b = false;
                if(payMaxAmount != null && payMaxAmount.doubleValue() >=500000){
                    b =true;
                }
                if(receiveMaxAmount != null && receiveMaxAmount.doubleValue() >=500000){
                    b =true;
                }
                if(!b){
                    continue;
                }
                // 非自然人
                if(receiveAmount.doubleValue() >=2000000 || payAmount.doubleValue() >=2000000){
                    log.warn("ss10 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                    result.setRuleSS010(DateUtils.formatDate(date,"")+"/"+DateUtils.formatDate(endTradeDate,""));
                    result.setSaveBoolean(true);
                    return;
                }
            }
        }
    }

    /**
     * 机构客户交易情况与其注册资本严重不符
     * 连续10个交易日，
     * 单位客户日均交易金额（10个交易日内总交易金额/10）是注册资本的20倍及以上且单位客户日均权益是注册资本的5倍及以上
     * @param build
     * @param result
     */
    @Override
    public void ruleSS011(AnalysisDto build, ResultDto result) {
        if(build.getCustomerType().equals(GlobalConstant.ACC_TYPE_PERSON)){
            // 自然人不跑该规则
            return;
        }
        // 成交金额大于0 存在交易的日期
        List<Date> tradeDateList = build.getFundSumList().stream()
//                .filter(r->r.getBusinessBalance().doubleValue()>0)
                .map(TbFundSum::getInitDate).distinct().sorted().collect(Collectors.toList());
        // 注册资本
        BigDecimal regAmt = build.getCstUnit().getRegAmt();
        for (Date date : tradeDateList) {
            if(GlobalConstant.fistDate != null && date.before(GlobalConstant.fistDate)){
                continue;
            }
            // 第10天的交易日期
            Date endTradeDate = this.getEndTradeDate(date,10);
//            Date endTradeDate = this.getEndTradeDate(date,30);
            // 10天内日终资金汇总记录
            List<TbFundSum> tbFundSumList = build.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(date) >= 0 && r.getInitDate().compareTo(endTradeDate) <= 0).collect(Collectors.toList());
            // 总交易金额
            BigDecimal totalTradeAmount = tbFundSumList.stream().map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 总期末权益
            BigDecimal totalCurrentAmount = tbFundSumList.stream().map(TbFundSum::getCurrentBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 日均交易金额
//            BigDecimal avgTradeAmount = DecimalUtils.divide(totalTradeAmount, 30);
            BigDecimal avgTradeAmount = DecimalUtils.divide(totalTradeAmount, 10);
            // 日均期末权益
//            BigDecimal avgCurrentAmount = DecimalUtils.divide(totalCurrentAmount, 30);
            BigDecimal avgCurrentAmount = DecimalUtils.divide(totalCurrentAmount, 10);
            // 日均交易金额 占 注册资本比例
            BigDecimal tradeDivide = DecimalUtils.divide(avgTradeAmount, regAmt);
            // 日均期末权益 占 注册资本比例
            BigDecimal currentDivide = DecimalUtils.divide(avgCurrentAmount, regAmt);
            if(tradeDivide.doubleValue() >= 20 && currentDivide.doubleValue() >=5){
                log.warn("ss11 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                result.setRuleSS011(DateUtils.formatDate(date,""));
                result.setSaveBoolean(true);
                return;
            }
        }
    }

    /**
     * 个人或法人客户，其联系人的手机号码或地址相同，并且账户的交易标的相同，交易方向相同或相反，交易金额较大
     * 联系电话、或手机号码或联系地址相同的2个客户（个人或单位）
     * 其账户均针对同一合约、交易方向相反发生10笔以上交易，且累计成交金额均大于等于1000万元
     */
    @Override
    public void ruleSS012_2() {
//        // 自然人信息
        List<TbCstPers> cstPersList = analysisMapper.selectAllCstPersInfo();
        // 法人信息
        List<LegalPersonInfo> personInfos = analysisMapper.selectAllLegalPersonInfo();
        // 账号
        Set<String> selfAccNoSet = new HashSet<>();
//        selfAccNoSet.add("802025165");
        cstPersList.forEach(item->selfAccNoSet.add(item.getSelfAccNo()));
        personInfos.forEach(item->selfAccNoSet.add(item.getInvestorId()));
        // 所有已添加到客户组的客户Set
        Set<String> allAccNoSet = new HashSet<>();
        // 客户组List
        List<List<String>> customerGroupList = new ArrayList<>();
        int c = 0;
        for (String accNo : selfAccNoSet) {
            c++;
            log.info("进度:{}/{}",c,selfAccNoSet.size());
            if(allAccNoSet.contains(accNo)){
                continue;
            }
            //联系电话Set
            Set<String> phoneSet = new HashSet<>();
            // 地址Set
            Set<String> addressSet = new HashSet<>();
            for (TbCstPers tbCstPers : cstPersList){
                if(tbCstPers.getSelfAccNo().equals(accNo)){
                    if(!StringUtils.isEmpty(tbCstPers.getContact1()) && !"@N".equals(tbCstPers.getContact1())){
                        phoneSet.add(tbCstPers.getContact1());
                    }
                    if(!StringUtils.isEmpty(tbCstPers.getContact2()) && !"@N".equals(tbCstPers.getContact2())){
                        phoneSet.add(tbCstPers.getContact2());
                    }
                    if(!StringUtils.isEmpty(tbCstPers.getContact3()) && !"@N".equals(tbCstPers.getContact3())){
                        phoneSet.add(tbCstPers.getContact3());
                    }
                    if(!StringUtils.isEmpty(tbCstPers.getAddress1()) && !"@N".equals(tbCstPers.getAddress1())){
                        addressSet.add(tbCstPers.getAddress1());
                    }
                    if(!StringUtils.isEmpty(tbCstPers.getAddress2()) && !"@N".equals(tbCstPers.getAddress2())){
                        addressSet.add(tbCstPers.getAddress2());
                    }
                    if(!StringUtils.isEmpty(tbCstPers.getAddress3()) && !"@N".equals(tbCstPers.getAddress3())){
                        addressSet.add(tbCstPers.getAddress3());
                    }
                    break;
                }
            }
            for (LegalPersonInfo personInfo : personInfos){
                if(personInfo.getInvestorId().equals(accNo)){
                    if(!StringUtils.isEmpty(personInfo.getTel()) && !"@N".equals(personInfo.getTel())){
                        phoneSet.add(personInfo.getTel());
                    }
                    if(!StringUtils.isEmpty(personInfo.getPhone()) && !"@N".equals(personInfo.getPhone())){
                        phoneSet.add(personInfo.getPhone());
                    }
                    if(!StringUtils.isEmpty(personInfo.getAddress()) && !"@N".equals(personInfo.getAddress())){
                        addressSet.add(personInfo.getAddress());
                    }
                }
            }
            if(phoneSet.isEmpty() && addressSet.isEmpty()){
                continue;
            }
            // 相同客户账户Set
            Set<String> accNoSet = new HashSet<>();
            // 自然人
            for (TbCstPers cstPers : cstPersList) {
                if(!cstPers.getSelfAccNo().equals(accNo)){
                    if(!StringUtils.isEmpty(cstPers.getContact1()) && phoneSet.contains(cstPers.getContact1())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                    if(!StringUtils.isEmpty(cstPers.getContact2()) && phoneSet.contains(cstPers.getContact2())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                    if(!StringUtils.isEmpty(cstPers.getContact3()) && phoneSet.contains(cstPers.getContact3())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                    if(!StringUtils.isEmpty(cstPers.getAddress1()) && addressSet.contains(cstPers.getAddress1())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                    if(!StringUtils.isEmpty(cstPers.getAddress2()) && addressSet.contains(cstPers.getAddress2())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                    if(!StringUtils.isEmpty(cstPers.getAddress3()) && addressSet.contains(cstPers.getAddress3())){
                        accNoSet.add(cstPers.getSelfAccNo());
                        continue;
                    }
                }
            }
            // 法人
            for (LegalPersonInfo personInfo : personInfos) {
                if(personInfo.getInvestorId().equals("802025198")){
                    int sss = 0;
                }
                if(personInfo.getInvestorId().equals(accNo)){
                    continue;
                }
                if(!StringUtils.isEmpty(personInfo.getTel()) && phoneSet.contains(personInfo.getTel())){
                    accNoSet.add(personInfo.getInvestorId());
                    continue;
                }
                if(!StringUtils.isEmpty(personInfo.getPhone()) && phoneSet.contains(personInfo.getPhone())){
                    accNoSet.add(personInfo.getInvestorId());
                    continue;
                }
                if(!StringUtils.isEmpty(personInfo.getAddress()) && addressSet.contains(personInfo.getAddress())){
                    accNoSet.add(personInfo.getInvestorId());
                    continue;
                }
            }
            if(allAccNoSet.containsAll(accNoSet)){
                continue;
            }
            if(!accNoSet.isEmpty()){
                accNoSet.add(accNo);
                allAccNoSet.addAll(accNoSet);
                customerGroupList.add(new ArrayList<>(accNoSet));
            }
        }
        log.info("SS012 共{}个客户组",customerGroupList.size());
        if (customerGroupList.isEmpty()){
            return;
        }
        //合约层数
        List<ProductContractDetail> contractDetails = productContractDetailMapper.selectAll();
        // 合约层数日期
        Map<Date, List<ProductContractDetail>> productMap = contractDetails.stream().collect(Collectors.groupingBy(ProductContractDetail::getTxDt));
        int index = 0;
        for (List<String> accNos : customerGroupList) {
            index++;
            log.info("SS012 进度:{}/{}",index,customerGroupList.size());
            // 信息相同的客户组
            for (int i = 0; i < accNos.size(); i++) {
                String accNo1 = accNos.get(i);
                if(i+1>=accNos.size()){
                    break;
                }
                // 查询客户1000W以上的日期
                List<Date> dateList1 = analysisMapper.selectFundSumbDateByAccNo(accNo1);
                for (int j = i+1; j < accNos.size(); j++) {
                    String accNo2 = accNos.get(j);
                    List<Date> dateList2 = analysisMapper.selectFundSumbDateByAccNo(accNo2);
                    // 交易日期交集
                    List<Date> dateList = new ArrayList<>();
                    for (Date date : dateList1) {
                        if(dateList2.contains(date)){
                            dateList.add(date);
                        }
                    }
                    if(dateList.isEmpty()){
                        continue;
                    }
                    int index2 = 0;
                    for (Date date : dateList) {
                        index2++;
                        log.info("SS012 进度:{}/{} - {}/{}",index,customerGroupList.size(),index2,dateList.size());
                        // 查询当天两个客户成交记录
                        List<TbFaturesTrade> faturesTradeList = analysisMapper.selectFaturesTradeByDateAndAcc(date,accNo1,accNo2);
                        // 按合约分组
                        Map<String, List<TbFaturesTrade>> codeMap = faturesTradeList.stream().collect(Collectors.groupingBy(TbFaturesTrade::getContractCode));
                        // 客户1总成交金额
                        BigDecimal totalAmount1 = BigDecimal.ZERO;
                        // 客户2总成交金额
                        BigDecimal totalAmount2 = BigDecimal.ZERO;
                        // 涉及合约
                        List<String> codeList = new ArrayList<>();

                        for (String code : codeMap.keySet()) {
                            // 合约层数
                            BigDecimal agmtcountCnt = BigDecimal.ZERO;
                            for (ProductContractDetail contractDetail : productMap.get(date)) {
                                if (contractDetail.getContractName().equals(code)) {
                                    agmtcountCnt = contractDetail.getAgmtcountCnt();
                                    break;
                                }
                            }
                            if (agmtcountCnt.doubleValue() == 0){
                                continue;
                            }

                            List<TbFaturesTrade> tbFaturesTradeList = codeMap.get(code);
                            // 按客户分组
                            Map<String, List<TbFaturesTrade>> accMap = tbFaturesTradeList.stream().collect(Collectors.groupingBy(TbFaturesTrade::getSelfAccNo));
                            if(accMap.keySet().size() != 2){
                                continue;
                            }
                            // 客户1 交易笔数
                            Set<String> count1 = new HashSet<>();
                            // 客户1交易金额
                            BigDecimal amount1 = BigDecimal.ZERO;
                            // 客户1收方手数
                            BigDecimal receiveNus1 = BigDecimal.ZERO;
                            // 客户1付方手数
                            BigDecimal payNus1 = BigDecimal.ZERO;
                            for (TbFaturesTrade faturesTrade : accMap.get(accNo1)) {
                                count1.add(faturesTrade.getEntrustNo());
                                // 成交金额
                                BigDecimal amount = faturesTrade.getBusinessAmount().multiply(faturesTrade.getSpringPriceType()).multiply(agmtcountCnt);
                                amount1 = amount1.add(amount);
                                if(faturesTrade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                    receiveNus1 = receiveNus1.add(faturesTrade.getBusinessAmount());
                                }else {
                                    payNus1 = payNus1.add(faturesTrade.getBusinessAmount());
                                }
                            }
                            // 客户1净手数
                            BigDecimal num1 = receiveNus1.subtract(payNus1);
                            if(count1.size()<10 || num1.doubleValue() == 0){
                                continue;
                            }
//                            if(num1.doubleValue() == 0){
//                                continue;
//                            }
                            // 客户2 交易笔数
                            Set<String> count2 = new HashSet<>();
                            // 客户2交易金额
                            BigDecimal amount2 = BigDecimal.ZERO;
                            // 客户2收方手数
                            BigDecimal receiveNus2 = BigDecimal.ZERO;
                            // 客户2付方手数
                            BigDecimal payNus2 = BigDecimal.ZERO;
                            for (TbFaturesTrade faturesTrade : accMap.get(accNo2)) {
                                count2.add(faturesTrade.getEntrustNo());
                                // 成交金额
                                BigDecimal amount = faturesTrade.getBusinessAmount().multiply(faturesTrade.getSpringPriceType()).multiply(agmtcountCnt);
                                amount2 = amount2.add(amount);
                                if(faturesTrade.getEntrustBs().equals(GlobalConstant.FUTURES_RECEIVE)){
                                    receiveNus2 = receiveNus2.add(faturesTrade.getBusinessAmount());
                                }else {
                                    payNus2 = payNus2.add(faturesTrade.getBusinessAmount());
                                }
                            }
                            // 客户2净手数
                            BigDecimal num2 = receiveNus2.subtract(payNus2);
                            if(count2.size()<10 || num2.doubleValue() == 0){
                                continue;
                            }
//                            if(num2.doubleValue() == 0){
//                                continue;
//                            }
                            // 同一合约交易金额
                            if(amount1.doubleValue()<10000000 || amount2.doubleValue() <10000000){
                                continue;
                            }
                            if(num1.doubleValue() >0 && num2.doubleValue()<0){
                                totalAmount1 = totalAmount1.add(amount1);
                                totalAmount2 = totalAmount2.add(amount2);
                                codeList.add(code);
                            }else if(num2.doubleValue()>0 && num1.doubleValue()<0){
                                totalAmount1 = totalAmount1.add(amount1);
                                totalAmount2 = totalAmount2.add(amount2);
                                codeList.add(code);
                            }
                        }
                        if(totalAmount1.doubleValue()>=10000000 && totalAmount2.doubleValue()>=10000000){
                            List<SaveDto012> saveList = new ArrayList<>();
                            saveList.add(new SaveDto012(date,accNo1,String.join(",",codeList)));
                            saveList.add(new SaveDto012(date,accNo2,String.join(",",codeList)));
                            analysisMapper.save012(saveList);
                        }
                    }

                }
            }
        }


    }

    /**
     * 自然人客户资产与年龄或职业不符
     * customer_risk_score中SCORE大于等于4的
     *  排除当天成交金额为0/银行卡无流水的日期
     * @param analysisDto
     * @param result
     */
    @Override
    public void ruleSS017(AnalysisDto analysisDto, ResultDto result) {
        //账号
        String selfAccNo = analysisDto.getSelfAccNo();
        //客户风险
        List<CustomerRiskScore> riskScoreList= analysisMapper.selectCustomerRiskScore(selfAccNo);
        if (riskScoreList != null && riskScoreList.size() > 0){
            // 成交金额不为0的日期
            Set<Date> dateSet = analysisDto.getFundSumList().stream().filter(r->r.getBusinessBalance().doubleValue()>0)
                    .map(TbFundSum::getInitDate).collect(Collectors.toSet());
            // 银行卡有流水日期
            Set<Date> dateSet1 = analysisDto.getBankTxnList().stream().map(TbBankTxn::getDate).collect(Collectors.toSet());
            dateSet.addAll(dateSet1);
            //满足条件集合
            List<String> list = new ArrayList<>();
            Date date = DateUtils.strToDate("2021-11-21","yyyy-MM-dd");
            for (CustomerRiskScore riskScore:riskScoreList) {
                if(dateSet.contains(riskScore.getGradeDt()) && riskScore.getGradeDt().after(date)){
                    list.add(DateUtils.formatDate(riskScore.getGradeDt(),""));
                }
            }
            if(list.size() > 0){
                result.setRuleSS017(String.join(",",list));
                result.setSaveBoolean(true);
            }
        }

    }

    /**
     * 条件1：60日内累计交易40次及以上，合计交易金额1000万元及以上；存在40日亏损，且发生交易的天数>亏损天数80%，60交易日内最终累计亏损（平仓盈亏+持仓盈亏）大于等于20万元，且最终亏损额占60日起始当日的权益的比例大于等于20%（（期初客户权益>0，且期初客户权益>=日均客户权益*0.8，且总亏损额度>=期初权益*20%或者（日均客户权益>0,且期初权益<日均权益*0.8，且总亏损额度>=日均权益*20%）建议不以日均权益作为计算，最后带入数据测算是否可实现与历史数据一致？:；
     * 条件2：符合条件1情况下，累计发生的手续费金额是交易所收取手续费的3倍及以上。
     * 交易所手续费如何提取（是按客户在观察期内累计产生的手续费与该观察期内交易所手续费的比）
     * @param build
     * @param result
     */
    @Override
    public void ruleSS015(AnalysisDto build, ResultDto result) {
        if(build.getHandingFreeAndLossList() == null || build.getHandingFreeAndLossList().isEmpty()){
            return;
        }
        // 交易日期
        //  交易日期通过日终资金汇总表获取
        List<Date> dateList = build.getFundSumList().stream().map(TbFundSum::getInitDate).distinct().sorted().collect(Collectors.toList());
        for (Date date : dateList) {
            // 60交易日最后一天
            Date endTradeDate = this.getEndTradeDate(date, 60);
            // 期货公司手续费
            BigDecimal handingFree = BigDecimal.ZERO;
            // 交易所手续费
            BigDecimal handingFree2 = BigDecimal.ZERO;
            for (HandingFreeAndLoss freeAndLoss : build.getHandingFreeAndLossList()) {
                if(freeAndLoss.getTxDt().compareTo(date)>=0 && freeAndLoss.getTxDt().compareTo(endTradeDate)<=0){
                    handingFree = handingFree.add(freeAndLoss.getHandlingFee());
                    handingFree2 = handingFree2.add(freeAndLoss.getHandlingFee2());
                }
            }
            BigDecimal divide1 = DecimalUtils.divide(handingFree, handingFree2);
            // 累计发生的手续费金额是交易所收取手续费的3倍及以上。
            if(divide1.doubleValue()<=3){
                continue;
            }
            // 交易次数
            Set<String> entrustNoSet = new HashSet<>();
            // 交易天数
            Set<Date> tradeDateSet = new HashSet<>();
            for (TbFaturesTrade faturesTrade : build.getFatureTradeList()) {
                if(faturesTrade.getInitDate().compareTo(date)>=0 && faturesTrade.getInitDate().compareTo(endTradeDate)<0){
                    entrustNoSet.add(faturesTrade.getEntrustNo());
                    tradeDateSet.add(faturesTrade.getInitDate());
                }
            }
            if(entrustNoSet.size()<=40 || tradeDateSet.size()<=40){
                continue;
            }
            // 亏损天数
            Set<Date> lossTradeDateSet = new HashSet<>();
            // 总亏损金额
            BigDecimal lossTotalAmount = BigDecimal.ZERO;
            for (LossDetail lossDetail : build.getLossDetailList()) {
                if (lossDetail.getTxDt().compareTo(date) >=0 && lossDetail.getTxDt().compareTo(endTradeDate)<=0) {
                    lossTotalAmount = lossTotalAmount.add(lossDetail.getLossAmount());
                    if(lossDetail.getLossAmount().doubleValue() <0){
                        lossTradeDateSet.add(lossDetail.getTxDt());
                    }
                }
            }
            if(lossTradeDateSet.size()<=40 || lossTotalAmount.doubleValue()>=-200000){
                continue;
            }
            // 交易天数占亏损天数占比
            BigDecimal divide = DecimalUtils.divide(tradeDateSet.size(), lossTradeDateSet.size());
            if(divide.doubleValue()<=0.8){
                continue;
            }
            // 期初客户权益>0，且期初客户权益>=日均客户权益*0.8，且总亏损额度>=期初权益*20%
            // 期初客户权益
            BigDecimal startBalance = BigDecimal.ZERO;
            // 总客户权益
            BigDecimal totalBalance = BigDecimal.ZERO;
            int _count = 0;
            for (TbFundSum tbFundSum : build.getFundSumList()) {
                if(tbFundSum.getInitDate().compareTo(date)>=0 && tbFundSum.getInitDate().compareTo(endTradeDate)<=0){
                    _count++;
                    if(tbFundSum.getInitDate().equals(date)){
                        startBalance = tbFundSum.getBeginBalance();
                    }
                    totalBalance = totalBalance.add(tbFundSum.getCurrentBalance());
                }
            }
            // 期初客户权益>0
            if(startBalance.doubleValue()<=0){
                continue;
            }
            BigDecimal avgBalance = DecimalUtils.divide(totalBalance,_count);
            BigDecimal avgBalance2 = avgBalance.multiply(BigDecimal.valueOf(0.8));
            // 期初客户权益>=日均客户权益*0.8
            if(startBalance.compareTo(avgBalance2)>=0){
                // 总亏损额度>=期初权益*20%
                startBalance = startBalance.multiply(BigDecimal.valueOf(0.2));
                if(lossTotalAmount.abs().compareTo(startBalance)<0){
                    continue;
                }
                log.warn("ss15 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                result.setRuleSS015(DateUtils.formatDate(date,"")+"/"+DateUtils.formatDate(endTradeDate,""));
                result.setSaveBoolean(true);
                return;
            }
            else if(avgBalance.doubleValue() >0 && startBalance.compareTo(avgBalance2)<0){
                // 总亏损额度>=日均权益*20%
                avgBalance = avgBalance.multiply(BigDecimal.valueOf(0.2));
                if(lossTotalAmount.abs().compareTo(avgBalance)<0){
                    continue;
                }
                log.warn("ss15 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
                result.setRuleSS015(DateUtils.formatDate(date,"")+"/"+DateUtils.formatDate(endTradeDate,""));
                result.setSaveBoolean(true);
                return;
            }

        }
    }

    /**
     *     条件1：存在休眠户重新使用时间（激活休眠账户时间）至销户时间（修改为资金转出或销户的时间）
     *     或者新开户时间至销户时间（修改为资金转出或者销户的时间）小于等于180天，
     *     且在使用期间的任一时点权益金额小于等于200万（且在180个交易日内客户任一交易日期末权益的峰值大于等于200万），
     *     且累计总成交金额大于等于1000万元。
     *     条件2：符合条件1情况下，
     *     在同一连续10个交易日内，
     *     资金户累计出金的金额占休眠户重新使用时间至销户时间或者新开户时间之销户时间任一时点权益金额的最大值的比例大于等于10%
     *     （资金账户累计出金金额占此期间期末权益峰值的比例大于等于10%），且出金后的期末资产等于0并（修改为或）销户。
     * 休眠/新开户
     * 180天内
     * 任意10日内
     * 累计成交金额大于等于1000W
     * 任意一日期末权益大于等于200W
     * 累计出金金额占10日内 最大期末权益比例大于等于10% 且出金后期末资产为0
     * @param build
     * @param result
     */
    @Override
    public void ruleSS020(AnalysisDto build, ResultDto result) {
        if(build.getFundSumList() == null || build.getFundSumList().isEmpty()){
            return;
        }
        // 开户日期
        Date openTime = null;
        if(build.getCstPers() != null){
            openTime = build.getCstPers().getOpenTime();
        }else if(build.getCstUnit() != null){
            openTime = build.getCstUnit().getOpenTime();
        }
        // 第一次日终资金交易流水记录
        Date initDate = build.getFundSumList().get(0).getInitDate();

        List<Date> openDateList = new ArrayList<>();
        if(openTime != null){
            openDateList.add(openTime);
        }
        // 查询激活时间
        List<CustomerActivateInfo> activateInfos = customerActivateInfoMapper.selectByAcc(build.getSelfAccNo());
        // 二 激活账户 180天内
        if(activateInfos != null && !activateInfos.isEmpty()){
            for (CustomerActivateInfo activateInfo : activateInfos) {
                Date date = DateUtils.strToDate(activateInfo.getActiveDt(), "yyyy-MM-dd");
                if(date != null){
                    openDateList.add(date);
                }
            }
        }
        for (Date openDate : openDateList) {
            int days = DateUtils.getDateDays(openDate, initDate);
            if(days >=180){
                return;
            }
            // 防止开户日期在数据范围之外导致交易取错
            if(openDate.before(initDate)){
                openDate = initDate;
            }

            // 180天内的交易日期
            Date endMaxTradeDate = this.getEndTradeDate(openDate, 180-days);
            Date finalOpenDate = openDate;
            List<Date> tradeDateList = build.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(finalOpenDate) >= 0 && r.getInitDate().compareTo(endMaxTradeDate) <= 0)
                    .map(TbFundSum::getInitDate).distinct().sorted().collect(Collectors.toList());
            for (Date date : tradeDateList) {
                // 10日
                for (int i = 0; i < 10; i++) {
                    Date endTradeDate = this.getEndTradeDate(date, i+1);
                    if(endTradeDate.after(endMaxTradeDate)){
                        continue;
                    }
                    // 累计成交金额
                    BigDecimal totalAmount =BigDecimal.ZERO;
                    // 累计出金金额
                    BigDecimal totalPayAmount = BigDecimal.ZERO;
                    // 权益最大值
                    BigDecimal maxBalance = BigDecimal.ZERO;
                    // 最后一天期末资产
                    BigDecimal endBalance = null;
                    for (TbFundSum tbFundSum : build.getFundSumList()) {
                        if(tbFundSum.getInitDate().compareTo(date)>=0 && tbFundSum.getInitDate().compareTo(endTradeDate)<=0){
                            totalAmount = totalAmount.add(tbFundSum.getBusinessBalance());
                            totalPayAmount = totalPayAmount.add(tbFundSum.getFundOut());
                            if (tbFundSum.getCurrentBalance().compareTo(maxBalance)>=0){
                                maxBalance = tbFundSum.getCurrentBalance();
                            }
                            if(tbFundSum.getInitDate().equals(endTradeDate)){
                                endBalance = tbFundSum.getCurrentBalance();
                            }
                        }
                    }
                    if(totalAmount.doubleValue()<10000000 || maxBalance.doubleValue()<2000000){
                        continue;
                    }
                    if(endBalance == null || endBalance.doubleValue() != 0){
                        continue;
                    }
                    // 出金金额占比
                    BigDecimal divide = DecimalUtils.divide(totalPayAmount, maxBalance);
                    if (divide.doubleValue()>=0.1) {
                        result.setRuleSS020(DateUtils.formatDate(date,"")+"-"+DateUtils.formatDate(endTradeDate,""));
                        result.setSaveBoolean(true);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 自然人客户或单位客户连续10个交易日内，
     * 使用5个及以上的MAC地址发生5次及以上的密码修改（资金密码>=5或交易密码>=5）
     * （不含新开户或重置密码后修改），
     * 且任一单日交易3个及（以上）合约，且连续10个交易日累计成交1000万元。
     * //  根据APPID过滤
     * @param build
     * @param result
     */
    @Override
    public void ruleSS029(AnalysisDto build, ResultDto result) {
        if(build.getPasswordUpdateInfoList()==null || build.getPasswordUpdateInfoList().isEmpty()){
            return;
        }
        // 使用的mac
        List<IpMacDto> macDtoList = analysisMapper.selectAllMacByAcc(build.getSelfAccNo());
        if(macDtoList == null || macDtoList.size() <5){
            return;
        }
        macDtoList.forEach(item -> {
            item.setMacAddress(item.getMacAddress().replace(":",""));
        });
        // 排除白名单
        macDtoList = macDtoList.stream().filter(r->!GlobalCache.macWitheSet.contains(r.getMacAddress()))
                .collect(Collectors.toList());
        if(macDtoList.size()<5){
            return;
        }

        // 交易日期
        List<Date> dateList = build.getFatureTradeList().stream()
                .map(TbFaturesTrade::getInitDate).distinct().sorted().collect(Collectors.toList());
        if(dateList.isEmpty()){
            return;
        }
        // 查询需排除的APP交易
        List<AppTradeRecord> appTradeRecords = appTradeRecordMapper.selectByAccNo(build.getSelfAccNo());
        Map<Date,Set<String>> appCountMap = new HashMap<>();
        if(appTradeRecords != null && !appTradeRecords.isEmpty()){
            for (AppTradeRecord appTradeRecord : appTradeRecords) {
                Date txDt = appTradeRecord.getTxDt();
                Set<String> orDefault = appCountMap.getOrDefault(txDt, new HashSet<>());
                orDefault.add(appTradeRecord.getOrderId());
                appCountMap.put(txDt,orDefault);
            }
        }

        for (Date date : dateList) {
            Date endTradeDate = this.getEndTradeDate(date, 10);
            // 5个及以上密码修改
            int sum = build.getPasswordUpdateInfoList().stream().filter(r -> r.getTradingday().compareTo(date) >= 0 && r.getTradingday().compareTo(endTradeDate) <= 0)
                    .mapToInt(PasswordUpdateInfo::getNum).sum();
            if(sum<5){
                continue;
            }
            // 10日累计成交金额
            BigDecimal reduce = build.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(date) >= 0 && r.getInitDate().compareTo(endTradeDate) <= 0)
                    .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(reduce.doubleValue()<10000000){
                continue;
            }
            // 任一单日交易3个及（以上）合约 排除APP交易
            Map<Date,Set<String>>  contractTradeMap = new HashMap<>();
            for (TbFaturesTrade faturesTrade : build.getFatureTradeList()) {
                Set<String> strings = appCountMap.get(faturesTrade.getInitDate());
                if(strings != null && !strings.isEmpty()){
                    if(strings.contains(faturesTrade.getEntrustNo())){
                        continue;
                    }
                }
                if(faturesTrade.getInitDate().compareTo(date)>=0 && faturesTrade.getInitDate().compareTo(endTradeDate)<=0){
                    Set<String> orDefault = contractTradeMap.getOrDefault(faturesTrade.getInitDate(), new HashSet<>());
                    orDefault.add(faturesTrade.getContractCode());
                    contractTradeMap.put(faturesTrade.getInitDate(),orDefault);
                }
            }
            boolean b = false;
            for (Date date1 : contractTradeMap.keySet()) {
                if (contractTradeMap.get(date1).size()>=3) {
                    b = true;
                }
            }
            if(!b){
                continue;
            }
            // MAC统计
            long count = macDtoList.stream().filter(r -> r.getTradeDate().compareTo(date) >= 0 && r.getTradeDate().compareTo(endTradeDate) <= 0)
                    .map(IpMacDto::getMacAddress).distinct().count();
            if(count<5){
                continue;
            }
            log.warn("ss29 - 客户：{} 日期：{}",build.getSelfAccNo(),DateUtils.formatDate(date,""));
            result.setRuleSS029(DateUtils.formatDate(date,""));
            result.setSaveBoolean(true);
            return;
        }

    }

    @Override
    public List<UnpopularContractsCount> getUnpopularContractsCountList() {
        log.info("冷门合约开始");
        List<UnpopularContractsCount> resultList = new ArrayList<>();
        List<ProductContractDetail> contractDetails = productContractDetailMapper.selectAll();
        List<UnpopularContractInfo> unpopularContractInfos = analysisMapper.selectContractInvalidDate();
        // 交易日期<合约代码,失效日期>
        Map<Date,Map<String,Date>> unpopularContractMap = new HashMap<>();
        for (UnpopularContractInfo contractInfo : unpopularContractInfos) {
            Map<String, Date> orDefault = unpopularContractMap.getOrDefault(contractInfo.getTxDt(), new HashMap<>());
            orDefault.put(contractInfo.getContractName(), contractInfo.getInvalidDate());
            unpopularContractMap.put(contractInfo.getTxDt(),orDefault);
        }
        unpopularContractInfos = null;
        int i = 0;
        for (ProductContractDetail contractDetail : contractDetails) {
            i++;
//            log.info("冷门合约进度: {} {}/{}",DateUtils.formatDate(contractDetail.getTxDt(),""),i,contractDetails.size());
            // 成交比值
            BigDecimal divide = DecimalUtils.divide(contractDetail.getTradeNums(), contractDetail.getProductTradeNums());
            // 持仓比值
            BigDecimal divide2 = DecimalUtils.divide(contractDetail.getPositionNums(), contractDetail.getProductPositionNums());
            if(divide.doubleValue()<=0.02 && divide2.doubleValue()<=0.02){
                // 失效日期
                Map<String, Date> orDefault = unpopularContractMap.getOrDefault(contractDetail.getTxDt(), new HashMap<>());
                // 失效日期
                Date invalidDate = orDefault.get(contractDetail.getContractName());
                if(invalidDate == null){
                    continue;
                }
                int days = DateUtils.getDateDays(contractDetail.getTxDt(), invalidDate);
                if(days<30){
                    continue;
                }
                UnpopularContractsCount contractsCount = new UnpopularContractsCount();
                contractsCount.setDate(contractDetail.getTxDt());
                contractsCount.setContractName(contractDetail.getContractName());
                contractsCount.setTradeDivide(divide);
                contractsCount.setPositionDivide(divide2);
                contractsCount.setAgmtcountCnt(contractDetail.getAgmtcountCnt());
                resultList.add(contractsCount);
            }
//            if(divide.doubleValue()<=0.02){
//                // 失效日期
//                Map<String, Date> orDefault = unpopularContractMap.getOrDefault(contractDetail.getTxDt(), new HashMap<>());
//                // 失效日期
//                Date invalidDate = orDefault.get(contractDetail.getContractName());
//                if(invalidDate == null){
//                    continue;
//                }
//                int days = DateUtils.getDateDays(contractDetail.getTxDt(), invalidDate);
//                if(days<150){
//                    continue;
//                }
//                UnpopularContractsCount contractsCount = new UnpopularContractsCount();
//                contractsCount.setDate(contractDetail.getTxDt());
//                contractsCount.setContractName(contractDetail.getContractName());
//                contractsCount.setTradeDivide(divide);
//                contractsCount.setAgmtcountCnt(contractDetail.getAgmtcountCnt());
//                resultList.add(contractsCount);
//            }
        }
        return resultList;
    }

    @Override
    public Map<Date, Map<String, BigDecimal>> getContractsAgmtCountCntMap() {
        Map<Date, Map<String, BigDecimal>> resultMap = new HashMap<>();
        List<ProductContractDetail> contractDetails = productContractDetailMapper.selectAll2();
        for (ProductContractDetail contractDetail : contractDetails) {
            Date txDt = contractDetail.getTxDt();
            String contractName = contractDetail.getContractName();
            BigDecimal agmtcountCnt = contractDetail.getAgmtcountCnt();
            Map<String, BigDecimal> orDefault = resultMap.getOrDefault(txDt, new HashMap<>());
            orDefault.put(contractName,agmtcountCnt);
            resultMap.put(txDt,orDefault);
        }
        return resultMap;
    }

    @Override
    public Map<Date, Set<String>> getIPMACWhiteList() {
        Map<Date, Set<String>> resultMap = new HashMap<>();

        List<Date> dateList = logMapper.selectAllDate();
        Collections.sort(dateList);
        int index = 0;
        for (Date date : dateList) {
            index++;
            log.info("进度:{} {}/{}",DateUtils.formatDate(date,""),index,dateList.size());
            List<Log> logList = logMapper.selectAllByDate(date);
            // 排除IP MAC为空数据 按照IPMAC分组
            Map<String, List<Log>> ipMacMap = logList.stream().filter(r -> !StringUtils.isEmpty(r.getIp()) && !r.getIp().equals("@N"))
                    .filter(r -> !StringUtils.isEmpty(r.getMac()) && !r.getMac().equals("@N"))
                    .collect(Collectors.groupingBy(r -> r.getIp() + r.getMac()));
            for (String ipMac : ipMacMap.keySet()) {
                Map<String, List<Log>> appMap = ipMacMap.get(ipMac).stream().collect(Collectors.groupingBy(Log::getAppId));
                for (List<Log> value : appMap.values()) {
                    // 客户数
                    long count = value.stream().map(Log::getInvestorId).distinct().count();
                    if(count>100){
                        log.info("IPMAC 日期：{} ip:{} mac:{}",DateUtils.formatDate(date,""),value.get(0).getIp(),value.get(0).getMac());
                        Set<String> orDefault = resultMap.getOrDefault(date, new HashSet<>());
                        orDefault.add(value.get(0).getIp()+value.get(0).getMac());
                        resultMap.put(date,orDefault);
                    }
                }
            }
            ipMacMap.clear();
            // SRC_IP MAC
            ipMacMap = logList.stream().filter(r -> !StringUtils.isEmpty(r.getSrcIp()) && !r.getSrcIp().equals("@N"))
                    .filter(r -> !StringUtils.isEmpty(r.getSrcMac()) && !r.getSrcMac().equals("@N"))
                    .collect(Collectors.groupingBy(r -> r.getSrcIp() + r.getSrcMac()));
            for (String ipMac : ipMacMap.keySet()) {
                Map<String, List<Log>> appMap = ipMacMap.get(ipMac).stream().collect(Collectors.groupingBy(Log::getAppId));
                for (List<Log> value : appMap.values()) {
                    // 客户数
                    long count = value.stream().map(Log::getInvestorId).distinct().count();
                    if(count>100){
                        log.info("SRCIPMAC 日期：{} ip:{} mac:{}",DateUtils.formatDate(date,""),value.get(0).getSrcIp(),value.get(0).getSrcMac());
                        Set<String> orDefault = resultMap.getOrDefault(date, new HashSet<>());
                        orDefault.add(value.get(0).getSrcIp()+value.get(0).getSrcMac());
                        resultMap.put(date,orDefault);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void computeRiskDate() {
        List<TbRiskHis> tbRiskHis = analysisMapper.selectAllRiskHis();
        // 按照客户分组
        Map<String, List<TbRiskHis>> accNoMap = tbRiskHis.stream().collect(Collectors.groupingBy(TbRiskHis::getSelfAccNo));
        int index = 0;
        for (String accNo : accNoMap.keySet()) {
            index ++;
//            log.info("风险等级  进度:{}/{}",index,accNoMap.keySet().size());
            List<TbRiskHis> tbRiskHisList = accNoMap.get(accNo);
            tbRiskHisList.forEach(item-> item.setRiskDate(DateUtils.strToDate(item.getTime(),"")));
            tbRiskHisList.sort(Comparator.comparing(TbRiskHis::getRiskDate));
            this.compute(tbRiskHisList,null,-1);
        }
        log.info("风险等级结束");
    }

    /**
     *
     * @param tbRiskHis
     */
    private void compute(List<TbRiskHis> tbRiskHis,Date nextDate,int lastIndex){
        int index = lastIndex+1;
        if(index >= tbRiskHis.size()){
            return;
        }
        TbRiskHis riskHis = tbRiskHis.get(index);
        if(lastIndex >=0){
            if(riskHis.getRiskDate().compareTo(nextDate)>0){
                TbRiskHis his = tbRiskHis.get(lastIndex);
                int days = DateUtils.getDateDays(riskHis.getRiskDate(), nextDate);
                RiskCountDto riskCountDto = new RiskCountDto(his.getSelfAccNo(),his.getRiskCode(),his.getRiskDate(),nextDate,riskHis.getRiskDate(),days);
                analysisMapper.insertRiskCount(riskCountDto);
            }
        }
        if("10".equals(riskHis.getRiskCode())){
            // 高风险 3个月
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(riskHis.getRiskDate());
            calendar.add(Calendar.MONTH,3);
            Date date = calendar.getTime();
            this.compute(tbRiskHis,date,index);
        }else if("11".equals(riskHis.getRiskCode())){
            // 较高 6个月
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(riskHis.getRiskDate());
            calendar.add(Calendar.MONTH,6);
            Date date = calendar.getTime();
            this.compute(tbRiskHis,date,index);
        }else if("12".equals(riskHis.getRiskCode())){
            // 较低 12个月
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(riskHis.getRiskDate());
            calendar.add(Calendar.MONTH,12);
            Date date = calendar.getTime();
            this.compute(tbRiskHis,date,index);
        }else if("13".equals(riskHis.getRiskCode())){
            // 低  24个月
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(riskHis.getRiskDate());
            calendar.add(Calendar.MONTH,24);
            Date date = calendar.getTime();
            this.compute(tbRiskHis,date,index);
        }else{
            // 无
            log.error("风险等级错误:{}-{}",riskHis.getSelfAccNo(),riskHis.getRiskCode());
            RiskCountDto riskCountDto = new RiskCountDto(riskHis.getSelfAccNo(),riskHis.getRiskCode(),riskHis.getRiskDate(),null,null,0);
            analysisMapper.insertRiskCount(riskCountDto);
        }
    }


    /**
     * 计算是否满足
     * 条件1：开户时间超过270天及以上的客户，连续270内未发生1笔交易；
     * 条件2：开户时间小于等于269天的客户，连续180天未发生1笔易；
     * @param analysisDto
     * @return  满足条件的最早交易日期
     */
    private List<SleepRange> computeSleepEndDate(AnalysisDto analysisDto){
        List<SleepRange> sleepRangeList = new ArrayList<>();
        // 无交易流水直接跳过
        if(analysisDto.getFatureTradeList() == null || analysisDto.getFatureTradeList().isEmpty()){
            return null;
        }
        // 因交易为2021年全年的  所以连续270天/180天内无交易需在21年内
        Date openDate = null;
        if(analysisDto.getCstPers()!= null){
            openDate = analysisDto.getCstPers().getOpenTime();
        }else if(analysisDto.getCstUnit() !=null){
            openDate = analysisDto.getCstUnit().getOpenTime();
        }
        if(openDate == null){
            return null;
        }
        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 交易日期 去重排序
        List<Date> tradeDateList = analysisDto.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate).distinct().sorted().collect(Collectors.toList());
        Date startDate;
        if(openDate.before(year)){
            // 开户日期在21年前
            startDate = year;
        }else {
            // 开户日期在21年及以后
            startDate = openDate;
        }
        for (int i = 0; i < tradeDateList.size(); i++) {
            Date endDate = tradeDateList.get(i);
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, endDate);
            if(sleepDays >180){
                // 计算距离开户日期天数
                int openDays = DateUtils.getDateDays(openDate, endDate);
                if(openDays <270 || sleepDays>270){
                    sleepRangeList.add(new SleepRange(startDate,endDate));
                }
            }
            startDate = endDate;
        }
        return sleepRangeList;
    }


    /**
     * 计算是否满足
     * 条件1：连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%）；
     * 条件2：开户时间小于等于269天的客户，连续180天未发生1笔易；
     * @param analysisDto
     * @return  满足条件的最早交易日期
     */
    private SleepRange computeSleepEndDate2(AnalysisDto analysisDto){
        // 无交易流水直接跳过
        if(analysisDto.getFatureTradeList() == null || analysisDto.getFatureTradeList().isEmpty()){
            return null;
        }
        // 因交易为2021年全年的  所以连续270天/180天内无交易需在21年内
        Date openDate = null;
        if(analysisDto.getAcc()!= null){
            openDate = analysisDto.getCstPers().getOpenTime();
        }else if(analysisDto.getCstUnit() !=null){
            openDate = analysisDto.getCstUnit().getOpenTime();
        }
        if(openDate == null){
            return null;
        }
        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 交易日期 去重排序
        List<TbFaturesTrade> collect = new ArrayList<>(analysisDto.getFatureTradeList());
        Date startDate;
        if(openDate.before(year)){
            // 开户日期在21年前 10月1日前不能有交易
            startDate = year;
        }else {
            // 开户日期在21年及以后
            startDate = openDate;
        }
        // 开户时间在270天内的 180天无交易
        for (TbFaturesTrade trade : collect) {
            Date endDate = trade.getInitDate();
            // 计算距离开户日期天数
            int openDays = DateUtils.getDateDays(openDate, endDate);
            if(openDays >=270){
                break;
            }
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, endDate);
            if(sleepDays>180){
                return new SleepRange(startDate,endDate);
            }
            startDate = endDate;
        }
        // 开户日期大于等于270天 连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%
        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : analysisDto.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        List<Date> tradeDateList = analysisDto.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());

        for (Date date : tradeDateList) {
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, date);
            if(sleepDays>270){
                // 计算距离开户日期天数
                int openDays = DateUtils.getDateDays(openDate, date);
                // 期间内总交易金额
                Date finalStartDate = startDate;
                BigDecimal totalAmount = analysisDto.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(finalStartDate) >= 0 && r.getInitDate().compareTo(date) <= 0)
                        .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(openDays >=270 && totalAmount.doubleValue()<=100000){
                    return new SleepRange(startDate,date);
                }
            }
            startDate = date;
        }
        return null;
    }

    /**
     * 计算是否满足
     * 条件1：连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%）；
     * 条件2：开户时间小于等于269天的客户，连续10天未发生1笔易；
     * 程序逻辑：每天都往前找270看看是否满足无交易或占用率小于等于5% 若开户日期小于269天的找到的间隔需满足至少10天
     * @param analysisDto
     * @return  满足条件的最早交易日期
     */
    private List<SleepRange> computeSleepEndDate4(AnalysisDto analysisDto){
        Date openDate = null;
        if(analysisDto.getCstPers()!= null){
            openDate = analysisDto.getCstPers().getOpenTime();
        }else if(analysisDto.getCstUnit() !=null){
            openDate = analysisDto.getCstUnit().getOpenTime();
        }
        if(openDate == null){
            return null;
        }
        List<SleepRange> sleepRangeList = new ArrayList<>();
        // 无交易流水直接跳过
        if(analysisDto.getFatureTradeList() == null || analysisDto.getFatureTradeList().isEmpty()){
            if(openDate.after(GlobalCache.tradeDateList.get(0))){
                // 开户日期在交易期内
                sleepRangeList.add(new SleepRange(openDate,GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }else {
                sleepRangeList.add(new SleepRange(GlobalCache.tradeDateList.get(0),GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }
            return sleepRangeList;
        }
        // 因交易为2021年全年的  所以连续270天/180天内无交易需在21年内

        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 交易日期 去重排序
        List<TbFaturesTrade> collect = new ArrayList<>(analysisDto.getFatureTradeList());
        Date startDate;
        if(openDate.before(year)){
            // 开户日期在21年前 10月1日前不能有交易
            startDate = year;
        }else {
            // 开户日期在21年及以后
            startDate = openDate;
        }
        // 开户时间在270天内的 10天无交易
        for (TbFaturesTrade trade : collect) {
            Date endDate = trade.getInitDate();
            // 计算距离开户日期天数
            int openDays = DateUtils.getDateDays(openDate, endDate);
            if(openDays >=270){
                break;
            }
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, endDate);
            if(sleepDays>10){
                sleepRangeList.add(new SleepRange(startDate,endDate,2));
            }
            startDate = endDate;
        }
        // 开户日期大于等于270天 连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%
        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : analysisDto.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        List<Date> tradeDateList = analysisDto.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());

        for (Date date : tradeDateList) {
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, date);
            if(sleepDays>270){
                // 计算距离开户日期天数
                int openDays = DateUtils.getDateDays(openDate, date);
                // 期间内总交易金额
                Date finalStartDate = startDate;
                BigDecimal totalAmount = analysisDto.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(finalStartDate) >= 0 && r.getInitDate().compareTo(date) <= 0)
                        .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(openDays >=270 && totalAmount.doubleValue()<=100000){
                    sleepRangeList.add(new SleepRange(startDate,date,1));
                }
            }
            startDate = date;
        }
        return sleepRangeList;
    }


    /**
     *
     * @param analysisDto
     * @param days
     * @return
     */
    private List<SleepRange> computeSleepEndDate5(AnalysisDto analysisDto,int days){
        Date openDate = null;
        if(analysisDto.getCstPers()!= null){
            openDate = analysisDto.getCstPers().getOpenTime();
        }else if(analysisDto.getCstUnit() !=null){
            openDate = analysisDto.getCstUnit().getOpenTime();
        }
        if(openDate == null){
            return null;
        }
        List<SleepRange> sleepRangeList = new ArrayList<>();
        // 无交易流水直接跳过
        if(analysisDto.getFatureTradeList() == null || analysisDto.getFatureTradeList().isEmpty()){
            if(openDate.after(GlobalCache.tradeDateList.get(0))){
                // 开户日期在交易期内
                sleepRangeList.add(new SleepRange(openDate,GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }else {
                sleepRangeList.add(new SleepRange(GlobalCache.tradeDateList.get(0),GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }
            return sleepRangeList;
        }
        // 270天内无交易或保证金占用率小于等于5%
        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : analysisDto.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        List<Date> tradeDateList = analysisDto.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());
        // 数据开始日期
        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 开户日期在数据开始日期之前的 默认为之前每天都有交易
        if(openDate.before(year)){
            tradeDateList.add(DateUtils.strToDate("2020-12-31","yyyy-MM-dd"));
            Collections.sort(tradeDateList);
        }
        Calendar calendar = Calendar.getInstance();
        for (Date date : tradeDateList) {
            // 距离开户日期天数
            int days1 = DateUtils.getDateDays(openDate, date);
            if(days1 <= days){
                continue;
            }
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,-270);
            Date startDate = calendar.getTime();
            long count = tradeDateList.stream().filter(r -> r.compareTo(startDate) >= 0 && r.before(date)).count();
            if(count>0){
                continue;
            }
            // 期间内总交易金额
            Date finalStartDate = startDate;
            BigDecimal totalAmount = analysisDto.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(finalStartDate) >= 0 && r.getInitDate().compareTo(date) <= 0)
                    .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(totalAmount.doubleValue()>100000){
                continue;
            }
            sleepRangeList.add(new SleepRange(startDate,date,1));
        }
        return sleepRangeList;
    }

    /**
     * 计算是否满足
     * 条件1：连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%（每日保证金占用比例=保证金金额/客户期末权益）小于等于5%）；
     * 条件2：开户时间小于等于269天的客户，连续180天未发生1笔易；
     * @param analysisDto
     * @return  满足条件的最早交易日期
     */
    private List<SleepRange> computeSleepEndDate3(AnalysisDto analysisDto){
        Date openDate = null;
        if(analysisDto.getCstPers()!= null){
            openDate = analysisDto.getCstPers().getOpenTime();
        }else if(analysisDto.getCstUnit() !=null){
            openDate = analysisDto.getCstUnit().getOpenTime();
        }
        if(openDate == null){
            return null;
        }
        List<SleepRange> sleepRangeList = new ArrayList<>();
        // 无交易流水直接跳过
        if(analysisDto.getFatureTradeList() == null || analysisDto.getFatureTradeList().isEmpty()){
            if(openDate.after(GlobalCache.tradeDateList.get(0))){
                // 开户日期在交易期内
                sleepRangeList.add(new SleepRange(openDate,GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }else {
                sleepRangeList.add(new SleepRange(GlobalCache.tradeDateList.get(0),GlobalCache.tradeDateList.get(GlobalCache.tradeDateList.size()-1)));
            }
            return sleepRangeList;
        }
        // 因交易为2021年全年的  所以连续270天/180天内无交易需在21年内

        Date year = DateUtils.strToDate("2021-01-01", "yyyy-MM-dd");
        // 交易日期 去重排序
        List<TbFaturesTrade> collect = new ArrayList<>(analysisDto.getFatureTradeList());
        Date startDate;
        if(openDate.before(year)){
            // 开户日期在21年前 10月1日前不能有交易
            startDate = year;
        }else {
            // 开户日期在21年及以后
            startDate = openDate;
        }
        // 开户时间在270天内的 180天无交易
        for (TbFaturesTrade trade : collect) {
            Date endDate = trade.getInitDate();
            // 计算距离开户日期天数
            int openDays = DateUtils.getDateDays(openDate, endDate);
            if(openDays >=270){
                break;
            }
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, endDate);
            if(sleepDays>180){
                sleepRangeList.add(new SleepRange(startDate,endDate,2));
            }
            startDate = endDate;
        }
        // 开户日期大于等于270天 连续270内未发生1笔交易或单日累计交易金额小于等于每日交易保证金占用率的5%
        // 保证金占用率小于5%的日期
        List<Date> excludeDateList = new ArrayList<>();
        for (TbFundSum tbFundSum : analysisDto.getFundSumList()) {
            // 每日保证金占用比例=保证金金额/客户期末权益
            BigDecimal divide = DecimalUtils.divide(tbFundSum.getHoldBalance(), tbFundSum.getCurrentBalance());
            if(divide.doubleValue()<=0.05){
                excludeDateList.add(tbFundSum.getInitDate());
            }
        }
        // 交易日期排除保证金占用率小的日期
        List<Date> tradeDateList = analysisDto.getFatureTradeList().stream().map(TbFaturesTrade::getInitDate)
                .filter(initDate -> !excludeDateList.contains(initDate)).distinct().sorted().collect(Collectors.toList());

        for (Date date : tradeDateList) {
            // 连续无交易天数
            int sleepDays = DateUtils.getDateDays(startDate, date);
            if(sleepDays>270){
                // 计算距离开户日期天数
                int openDays = DateUtils.getDateDays(openDate, date);
                // 期间内总交易金额
                Date finalStartDate = startDate;
                BigDecimal totalAmount = analysisDto.getFundSumList().stream().filter(r -> r.getInitDate().compareTo(finalStartDate) >= 0 && r.getInitDate().compareTo(date) <= 0)
                        .map(TbFundSum::getBusinessBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(openDays >=270 && totalAmount.doubleValue()<=100000){
                    sleepRangeList.add(new SleepRange(startDate,date,1));
                }
            }
            startDate = date;
        }
        return sleepRangeList;
    }

    /**
     * 获取连续交易日期的最后一天
     * @param startDate 开始日期
     * @param days      连续天数
     * @return
     */
    private Date getEndTradeDate(Date startDate,int days){
        // 最后一天的交易日期下标
        int index = 0;
        for (int i = 0; i < GlobalCache.tradeDateList.size(); i++) {
            if(GlobalCache.tradeDateList.get(i).equals(startDate)){
                index = i;
                break;
            }
        }
        index += (days-1);
        if(index >= GlobalCache.tradeDateList.size()){
            index = GlobalCache.tradeDateList.size()-1;
        }
        if(index <0){
            index = 0;
        }
        // 最后一天的交易日期
        return GlobalCache.tradeDateList.get(index);
    }
    /**
     * 获取连续的交易日期
     * @param startDate 开始日期
     * @param days      连续天数
     * @return
     */
    private List<Date> getEndTradeDateList(Date startDate,int days){
        // 最后一天的交易日期下标
        int startIndex = 0;
        for (int i = 0; i < GlobalCache.tradeDateList.size(); i++) {
            if(GlobalCache.tradeDateList.get(i).equals(startDate)){
                startIndex = i;
                break;
            }
        }
        List<Date> result = new ArrayList<>();
        int index = startIndex + (days-1);
        if(index >= GlobalCache.tradeDateList.size()){
            index = GlobalCache.tradeDateList.size()-1;
        }
        if(index <0){
            index = 0;
        }else {
            for (int i = startIndex; i <=index ; i++) {
                result.add(GlobalCache.tradeDateList.get(i));
            }
        }
        // 最后一天的交易日期
        return result;
    }
    /**
     * 获取连续交易日期的最一天
     * @param startDate 结束日期
     * @param days      连续天数
     * @return
     */
    private Date getStartTradeDate(Date startDate,int days){
        // 最后一天的交易日期下标
        int index = 0;
        for (int i = 0; i < GlobalCache.tradeDateList.size(); i++) {
            if(GlobalCache.tradeDateList.get(i).equals(startDate)){
                index = i;
                break;
            }
        }
        index -= (days-1);
        if(index >= GlobalCache.tradeDateList.size()){
            index = GlobalCache.tradeDateList.size()-1;
        }
        if(index <0){
            index = 0;
        }
        // 第一天的交易日期
        return GlobalCache.tradeDateList.get(index);
    }

}
