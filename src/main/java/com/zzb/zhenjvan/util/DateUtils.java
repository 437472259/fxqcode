package com.zzb.zhenjvan.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * @author lisai
 * @title: DateUtils
 * @projectName aml
 * @description:
 * @date 2020/7/9 2:22
 */
@Slf4j
public class DateUtils {

    private static List<String> day = new ArrayList<>();
    static {
        day.add("20220101");
        day.add("20220102");
        day.add("20220103");
        day.add("20220211");
        day.add("20220212");
        day.add("20220213");
        day.add("20220214");
        day.add("20220215");
        day.add("20220216");
        day.add("20220217");
        day.add("20220403");
        day.add("20220404");
        day.add("20220405");
        day.add("20220501");
        day.add("20220502");
        day.add("20220503");
        day.add("20220504");
        day.add("20220505");
        day.add("20220612");
        day.add("20220613");
        day.add("20220614");
        day.add("20220919");
        day.add("20220920");
        day.add("20220921");
        day.add("20221001");
        day.add("20221002");
        day.add("20221003");
        day.add("20221004");
        day.add("20221005");
        day.add("20221006");
        day.add("20221007");
    }

    /**
     * 多少天以前的工作日
     * @param startDate
     * @param workDay
     * @return
     */
    public static Date getWorkDay(Date startDate, int workDay) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(startDate);
        for (int i = 0; i < workDay;) {
            c1.set(Calendar.DATE, c1.get(Calendar.DATE) - 1);
            int week = getWeek(c1.getTime());
            if (week != 7 && week != 6 && !day.contains(DateUtils.formatDate(c1.getTime(), "yyyyMMdd"))) {
                i++;
            }
        }
        return c1.getTime();
    }


    /**
     * 判定是否为工资，利息等等（下一自然月的同一天的前后3天）
     * @param date1
     * @param date2
     * @return
     */
    public static boolean wagesDateInterval(Date date1,Date date2){
        int months = getDateMonths(date1, date2);
        if (months == 1){
            int day1 = Integer.parseInt(formatDate(date1, "dd"));
            int day2 = Integer.parseInt(formatDate(date2, "dd"));
            int i = day1 - day2;
            if (i>=-3 && i <= 3){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
    /**
     * 格式化日期
     * @param date
     * @param pattern 默认yyyy-MM-dd
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    /**
    * @Description: 获取当前日期的开始时间
    * @Param:
    * @return:
    * @Author: hanyi
    * @Date: 2022/1/20 0020 15:31
    */
    public static Date getBeginTime(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    /**
     * @Description: 获取当前日期的指定时间（指定小时）
     * @Param:
     * @return:
     * @Author: hanyi
     * @Date: 2022/1/20 0020 15:31
     */
    public static Date getZDTime(Date date,int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, i);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 日期缩小精度
     * @param date
     * @param pattern 默认yyyy-MM-dd
     * @return
     */
    public static Date dateFormatDate(Date date, String pattern) {
        if (pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String format = sdf.format(date);
        return strToDate(format,pattern);
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @param pattern 默认yyyyMMdd
     * @return
     */
    public static Date strToDate(String date, String pattern) {
        if (pattern.isEmpty()) {
            pattern = "yyyyMMdd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期转换异常",e);
        }
        return null;
    }

    private boolean isEmpty(String pattern){
        return pattern.isEmpty() || "@N".equals(pattern);
    }

    /**
     * 计算两个日期字符串相隔多天
     *
     * @param start   起始日期字符串
     * @param end     终止日期字符串
     * @param pattern 默认yyyy-MM-dd
     * @return
     */
    public static int sumDay(String start, String end, String pattern) {
        if (pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        Date startDate = strToDate(start, pattern);
        Date endDate = strToDate(end, pattern);
        if (startDate != null && endDate != null) {
            int nDay = (int) ((startDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000));
            return nDay;
        } else {
            return 0;
        }
    }

    /**
     * 计算两个日期相差多少天
     * @param startDate
     * @param endDate
     * @return
     */
    public static int sumDay(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            return (int) ((startDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000));
        } else {
            return 0;
        }
    }

    public static String getCurrentYear() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year;

    }

    public static String getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 两个日期间隔月数数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public static int getDateMonths(Date startDate, Date endDate) {
        Calendar  c1  =  Calendar.getInstance();
        c1.setTime(startDate);
        Calendar  c2  =  Calendar.getInstance();
        c2.setTime(endDate);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int fromMonth = c1.get(Calendar.MONTH);
        int toMonth = c2.get(Calendar.MONTH);
        return (toMonth - fromMonth)+(year2-year1)*12;
    }

    /**
     * 两个日期间隔天数
     *
     * @param date1 开始日期
     * @param date2 结束日期
     * @return
     */
    public static int getDateDays(String date1, String date2) {
        long betweenDate = 0L;
        try {
            //设置转换的日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //开始时间
            Date startDate = sdf.parse(date1);
            //结束时间
            Date endDate = sdf.parse(date2);
            //得到相差的天数 betweenDate
            betweenDate = (endDate.getTime() - startDate.getTime()) / (60 * 60 * 24 * 1000);
        } catch (ParseException e) {
            log.error("日期转换异常", e);
        }
        return Integer.parseInt(betweenDate + "");
    }

    public static int getDateDays(Date startDate, Date endDate) {
        if(startDate == null || endDate == null){
            return 0;
        }
        //得到相差的天数 betweenDate
        long betweenDate = (endDate.getTime() - startDate.getTime()) / (60 * 60 * 24 * 1000);
        return Integer.parseInt(betweenDate + "");
    }

    /**
     * 两个日期间隔小时
     *
     * @param date1 开始日期
     * @param date2 结束日期
     * @return
     */
    public static double getDateHours(String date1, String date2) {
        double betweenDate = 0L;
        try {
            //设置转换的日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //开始时间
            Date startDate = sdf.parse(date1);
            //结束时间
            Date endDate = sdf.parse(date2);
            //得到相差的小时 betweenDate
            betweenDate = ((double) (endDate.getTime() - startDate.getTime()) / (double) (60 * 60 * 1000));
        } catch (ParseException e) {
            log.error("日期转换异常", e);
        }
        BigDecimal bg = new BigDecimal(betweenDate);
        double f1 = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        return f1;
    }
    /**
     * 两个日期间隔小时
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public static double getDateHours(Date startDate, Date endDate) {
        double betweenDate = ((double) (endDate.getTime() - startDate.getTime()) / (double) (60 * 60 * 1000));;
        BigDecimal bg = new BigDecimal(betweenDate);
        double f1 = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        return f1;
    }



    /**
     * 两个日期间隔多少分钟
     *
     * @param date1 开始日期
     * @param date2 结束日期
     * @return
     */
    public static double getDateMinutes(String date1, String date2) {
        double betweenDate = 0L;
        try {
            //设置转换的日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //开始时间
            Date startDate = sdf.parse(date1);
            //结束时间
            Date endDate = sdf.parse(date2);
            //得到相差的分钟 betweenDate
            betweenDate = ((double) (endDate.getTime() - startDate.getTime()) / (double) (60 * 1000));
        } catch (ParseException e) {
            log.error("日期转换异常", e);
        }
        BigDecimal bg = new BigDecimal(betweenDate);
        double f1 = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        return f1;
    }

    /**
     * 两个日期间隔多少分钟
     * @param startDate
     * @param endDate
     * @return
     */
    public static double getDateMinutes(Date startDate, Date endDate) {
        double betweenDate = 0L;
        //得到相差的分钟 betweenDate
        betweenDate = ((double) (endDate.getTime() - startDate.getTime()) / (double) (60 * 1000));
        BigDecimal bg = new BigDecimal(betweenDate);
        double f1 = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        return f1;
    }

    /**
     * 获取两个时间相差秒数
     * @param startDate     开始时间
     * @param endDate       结束时间
     * @return
     */
    public static long getDiffSecond(Date startDate,Date endDate){
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    /**
     * 获取日期是星期几
     * @param date
     * @return
     */
    public static int getWeek(Date date){
        int[] weekDays = {7,1,2,3,4,5,6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0){
            week = 0;
        }
        return weekDays[week];
    }

    /**
     * 获取日期的月份
     * @param date
     * @return
     */
    public static int getMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期 小时
     * @param date
     * @return
     */
    public static int getHour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * 获取日期 天
     * @param date
     * @return
     */
    public static int getDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }


    public static void sort(List<Date> dataList){
        Collections.sort(dataList, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                if(o1 != null && o2 != null){
                    long t = o1.getTime() - o2.getTime();
                    return t > 0 ? 1 : -1;
                }else{
                    return -1;
                }
            }
        });
    }

    /**
     * 获取日期所在月份的第一天
     * @param date
     * @return
     */
    public static Date getMonthFirstDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 时
        cal.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        cal.set(Calendar.MINUTE, 0);
        // 秒
        cal.set(Calendar.SECOND, 0);
        // 毫秒
        cal.set(Calendar.MILLISECOND, 0);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }

    /**
     * 获取当前月份的最后一天
     * @param date
     * @return
     */
    public static Date getMonthLastDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 时
        cal.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        cal.set(Calendar.MINUTE, 0);
        // 秒
        cal.set(Calendar.SECOND, 0);
        // 毫秒
        cal.set(Calendar.MILLISECOND, 0);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();
    }

    /**
     * 判断两个日期是否在同一月内
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean isInOneMonth(Date startDate,Date endDate){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(startDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(endDate);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        if (year1 == year2 && month1==month2) {
            return true;
        }
        return false;
    }

    /**
     * 获取连续的自然月
     * @param localDates
     * @return list中每个子集有且只有两个元素
     */
    public static List<List<Date>> getContinuousLocalDateByMonth(List<Date> localDates){
        List<List<Date>> resultList = new ArrayList<>();
        Collections.sort(localDates);
        for (int i = 0; i < localDates.size()-2; i++) {
            Date startDate = localDates.get(i);
            Date endDate = localDates.get(i + 1);
            if(getDateMonths(startDate,endDate) == 1){
                List<Date> list = new ArrayList<>();
                list.add(startDate);
                list.add(endDate);
                resultList.add(list);
            }
        }
        return resultList;
    }

    /**
     * 筛选指定交易日期后指定天数内的日期
     * @param dateList
     * @param startDate 开始日期
     * @param days   天数
     * @return
     */
    public static List<Date> filterByDateRange(List<Date> dateList,Date startDate,int days){
        List<Date> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, days);
        Date endDate = calendar.getTime();
        for (Date date : dateList) {
            if(date.before(endDate)){
                if(date.compareTo(startDate) >= 0){
                    result.add(date);
                }
            }else {
                break;
            }
        }
        return result;
    }

    /**
     * 筛选连续交易的日期 - 按天连续
     * @param dateList
     * @return
     */
    public static List<Set<Date>> countContinuousTradeDateByDay(List<Date> dateList){
        List<Set<Date>> resultList = new ArrayList<>();
        for (int i = 0; i < dateList.size()-1; i++) {
            Date date = dateList.get(i);
            Date date1 = dateList.get(i + 1);
            if(DateUtils.getDateDays(date, date1) == 1){
                Set<Date> dateSet = new HashSet<>();
                dateSet.add(date);
                dateSet.add(date1);
                resultList.add(dateSet);
            }
        }
        // 合并连续的日期
        for (int i = 0; i < resultList.size()-1; i++) {
            for (int j = i+1; j < resultList.size(); j++) {
                List<Date> list = new ArrayList<>(resultList.get(i));
                List<Date> list2 = new ArrayList<>(resultList.get(j));
                list.retainAll(list2);
                if (!list.isEmpty()) {
                    // 有交集
                    Set<Date> set = resultList.get(i);
                    Set<Date> set1 = resultList.get(j);
                    set.addAll(set1);
                    resultList.remove(j);
                    j--;
                }
            }
        }
        return resultList;
    }

    /**
     * 筛选连续交易的日期 - 按月连续
     * @param dateList
     * @return
     */
    public static List<Set<Date>> countContinuousTradeDateByMonth(List<Date> dateList){
        Collections.sort(dateList);
        Collections.reverse(dateList);
        // 连续交易的日期
        List<Set<Date>> keepTradeMonthList = new ArrayList<>();
        // 已确定的连续交易日期
        Set<Date> keepMonthSet = new HashSet<>();
        for (int i = 0; i < dateList.size()-1; i++) {
            Date secondMonth = dateList.get(i);
            if (keepMonthSet.contains(secondMonth)) {
                continue;
            }
            Set<Date> dateSet = new HashSet<>();
            for (int j = i + 1; j < dateList.size(); j++) {
                Date firstMonth = dateList.get(j);
                if (DateUtils.getDateMonths(firstMonth, secondMonth) == 1) {
                    dateSet.add(firstMonth);
                    dateSet.add(secondMonth);
                    secondMonth = firstMonth;
                } else {
                    break;
                }
            }
            if (!dateSet.isEmpty()) {
                keepMonthSet.addAll(dateSet);
                keepTradeMonthList.add(dateSet);
            }
        }
        return keepTradeMonthList;
    }

    /**
     * 多少天前的时间
     * @param date
     * @param i
     * @param pattern
     * @return
     */
    public static Date historyTime(Date date,int i,String pattern){
        if (pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -i);
            return cal.getTime();
        }catch (Exception e){
            log.error("日期转换错误",e);
            return null;
        }
    }

    /**
     * 前多少月
     * @param date
     * @param i
     * @return
     */
    public static Date historyMonth(Date date,int i){
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -i);
            return cal.getTime();
        }catch (Exception e){
            log.error("日期转换错误",e);
            return null;
        }
    }

    /**
     * 计算两个date相差多少秒 忽略年月日
     * @param startDate
     * @param endDate
     * @return
     */
    public static long countTimeDiffSeconds(Date startDate,Date endDate){
        LocalTime startTime = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault()).toLocalTime();
        return startTime.until(endTime, SECONDS);
    }

    /**
     * date转LocalTime
     * @return
     */
    public static LocalTime dateToLocalTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * 获取日期所在年及第几周 如：2021年8月30日为2021的第36周 返回值为202136
     * @param date
     * @return
     */
    public static int getYearAndWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 第几周
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        // 年份
        int year = cal.get(Calendar.YEAR);
        return year*100 +week;
    }

    /**
     * 判断两个日期是否在同一周内
     * @return
     */
    public static boolean datesIsInOneWeek(Date date1,Date date2){
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(date1);
        cal.setTime(date2);
        return cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 判断两个时间是否在同一天内
     * @param date1
     * @param date2
     * @return
     */
    public static boolean datesIsOneDay(Date date1,Date date2){
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(date1);
        cal.setTime(date2);
        return cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得时间区间的分区
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dataRegion(String startTime,String endTime){
        Set<String> set = new HashSet<>();
        Date date = strToDate(startTime,"yyyy-MM-dd");
        endTime = formatDate(strToDate(endTime, "yyyy-MM-dd"), "yyyyMM");
        do {
            startTime = DateUtils.formatDate(date, "yyyyMM");

            set.add("p"+ startTime);
            //前一月时间
            date = DateUtils.historyMonth(date, -1);
        }while (!startTime.equals(endTime));
        return String.join(",",set);
    }

}
