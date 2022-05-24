package com.zzb.zhenjvan.entity;


import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
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
@Table( value ="tb_fatures_trade" )
public class TbFaturesTrade   {


   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

   	@Column(value = "Cst_name" )
	private String cstName;
	/**
	 * 委托编号
 	 */
   	@Column(value = "Entrust_no" )
	private String entrustNo;

	/**
	 * 成交日期
	 */
   	@Column(value = "Init_date" )
	private Date initDate;

	/**
	 * 成交时间
	 */
   	@Column(value = "Curr_time" )
	private Date currTime;

   	@Column(value = "Contract_code" )
	private String contractCode;

   	@Column(value = "Contract_name" )
	private String contractName;

   	@Column(value = "Futures_direction" )
	private String futuresDirection;

   	@Column(value = "Entrust_bs" )
	private String entrustBs;

   	@Column(value = "Hedge_type" )
	private String hedgeType;

   	@Column(value = "Business_amount" )
	private BigDecimal businessAmount;

   	@Column(value = "Spring_price_type" )
	private BigDecimal springPriceType;

	/**
	 * 成交时间
	 */
   	@Column(value = "date_time" )
	private Date dateTime;

}
