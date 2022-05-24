package com.zzb.zhenjvan.entity;


import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Description
 * @Author  Hunter
 * @Date 2022-02-27
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table( value ="tb_fund_sum" )
public class TbFundSum {

   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

   	@Column(value = "Cst_name" )
	private String cstName;

   	@Column(value = "Init_date" )
	private Date initDate;

   	@Column(value = "Cur" )
	private String cur;

   	@Column(value = "Begin_balance" )
	private BigDecimal beginBalance;

	/**
	 * 期末权益
	 */
	@Column(value = "Current_balance" )
	private BigDecimal currentBalance;

   	@Column(value = "Futu_impawn_balance" )
	private BigDecimal futuImpawnBalance;

	/**
	 * 当日持仓保证金
	 */
	@Column(value = "Hold_balance" )
	private BigDecimal holdBalance;

   	@Column(value = "Exch_hold_balance" )
	private BigDecimal exchHoldBalance;

   	@Column(value = "Superadd_balance" )
	private BigDecimal superaddBalance;

   	@Column(value = "Drop_profit" )
	private BigDecimal dropProfit;

   	@Column(value = "Risk_rate" )
	private BigDecimal riskRate;

   	@Column(value = "Risk_rate_n" )
	private BigDecimal riskRateN;

   	@Column(value = "Frozen_open_fare" )
	private BigDecimal frozenOpenFare;

   	@Column(value = "Payment_balance" )
	private BigDecimal paymentBalance;

   	@Column(value = "Business_amount" )
	private BigDecimal businessAmount;

	/**
	 * 成交金额
 	 */
   	@Column(value = "Business_balance" )
	private BigDecimal businessBalance;

   	@Column(value = "Fund_in" )
	private BigDecimal fundIn;

	/**
	 * 出金
 	 */
   	@Column(value = "Fund_out" )
	private BigDecimal fundOut;

   	@Column(value = "Deliver_fare" )
	private BigDecimal deliverFare;

   	@Column(value = "Cash_balance" )
	private BigDecimal cashBalance;

}
