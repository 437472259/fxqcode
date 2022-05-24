package com.zzb.zhenjvan.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数学计算类
 * @author flynn
 * @date 2020/9/25 16:07
 */
public class DecimalUtils {

    public static DecimalFormat decimalFormat = new DecimalFormat("0.00%");

    public static BigDecimal divide(int divisor,int dividend){
        if(dividend == 0){
            return BigDecimal.valueOf(0);
        }else {
            return BigDecimal.valueOf(divisor).divide(BigDecimal.valueOf(dividend),4, RoundingMode.HALF_UP);
        }
    }
    public static BigDecimal multipleDivide(int divisor,int dividend){
        if(dividend == 0){
            return BigDecimal.valueOf(0);
        }else {
            return BigDecimal.valueOf(divisor).divide(BigDecimal.valueOf(dividend),2, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal divide(long divisor,long dividend){
        if(dividend == 0){
            return BigDecimal.valueOf(0);
        }else {
            return BigDecimal.valueOf(divisor).divide(BigDecimal.valueOf(dividend),4, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal divide(BigDecimal divisor,long dividend){
        if(dividend == 0){
            return BigDecimal.valueOf(0);
        }else {
            return divisor.divide(BigDecimal.valueOf(dividend),4, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal divide(BigDecimal divisor,BigDecimal dividend){
        if(dividend.compareTo(BigDecimal.valueOf(0)) <1){
            return BigDecimal.valueOf(0);
        }else {
            return divisor.divide(dividend,4, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal multipleDivide(BigDecimal divisor,BigDecimal dividend){
        if(dividend.compareTo(BigDecimal.valueOf(0)) <1){
            return BigDecimal.valueOf(0);
        }else {
            return divisor.divide(dividend,2, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal multiply(int multiplicand,double mc){
        return BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(mc));
    }
    public static BigDecimal multiply(BigDecimal multiplicand,long mc){
        return multiplicand.multiply(BigDecimal.valueOf(mc));
    }
    public static BigDecimal multiply(BigDecimal multiplicand,BigDecimal mc){
        return multiplicand.multiply(mc);
    }
    public static BigDecimal multiply(double multiplicand,double mc){
        return BigDecimal.valueOf(multiplicand).multiply(BigDecimal.valueOf(mc));
    }

    public static BigDecimal multiply(int multiplicand,BigDecimal mc){
        return BigDecimal.valueOf(multiplicand).multiply(mc);
    }

    public static String divideAndToPercent(int divisor,int dividend){
        return DecimalUtils.decimalFormat.format(DecimalUtils.divide(divisor,dividend));
    }
    public static String divideAndToPercent(BigDecimal divisor,BigDecimal dividend){
        return DecimalUtils.decimalFormat.format(DecimalUtils.divide(divisor,dividend));
    }

    /**
     * 获取金额的小数位的最后一个值
     * @param amount
     * @return
     */
    public static int getLastNumber(BigDecimal amount){
        String toString = String.valueOf(amount.doubleValue());
        return Integer.parseInt(toString.substring(toString.length()-1));
    }


    /**
     * 风险评估比例计算
     * 结果计算结果保留8为小数
     * 不为0的情况最小取值为0.0001
     * 返回值保留4为小数
     * @param divisor
     * @param dividend
     * @return
     */
    public static BigDecimal riskScoreDivide(int divisor,int dividend){
        if(divisor == 0 || dividend ==0){
            return BigDecimal.valueOf(0);
        }
        BigDecimal divide = BigDecimal.valueOf(divisor).divide(BigDecimal.valueOf(dividend),8, RoundingMode.HALF_UP);
        if(divide.doubleValue() >0 && divide.doubleValue() <0.0001){
            divide = BigDecimal.valueOf(0.0001);
        }
        divide = divide.setScale(4,RoundingMode.HALF_UP);
        return divide;
    }


}


