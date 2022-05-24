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
@Table( value ="tb_bank_txn" )
public class TbBankTxn   {


	/**
	 * 资金账号
	 */
   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

	/**
	 * 客户名称
	 */
   	@Column(value = "Cst_name" )
	private String cstName;

	/**
	 * 流水序号
	 */
   	@Column(value = "Ticd" )
	private String ticd;

	/**
	 * 交易日期
	 */
   	@Column(value = "Date" )
	private Date date;

	/**
	 * 交易时间
	 */
   	@Column(value = "Time" )
	private Date time;

	/**
	 * 交易金额
	 */
   	@Column(value = "Amt" )
	private BigDecimal amt;

	/**
	 * 账户余额
	 */
   	@Column(value = "Balance" )
	private BigDecimal balance;

	/**
	 * 收付标识 10 入金， 11 出金
	 */
   	@Column(value = "Lend_flag" )
	private String lendFlag;

	/**
	 * 转账方式 11 银期转账， 12 手工出
入金
	 */
   	@Column(value = "Entrust_flag" )
	private String entrustFlag;

	/**
	 * 交易摘要/备注
	 */
   	@Column(value = "Purpose" )
	private String purpose;

	/**
	 * 结算银行名称 01 工商银行， 02 农业银
行， 03 中国银行， 04 建
设银行， 05 交通银行，
06 浦发银行， 07 兴业银
行， 08 汇丰银行， 09 光
大银行， 10 招商银行，
11 中信银行， 12 民生银
行， 13 平安银行， 16 广
发银行， 99 其他银行
	 */
   	@Column(value = "Bank_name" )
	private String bankName;

	/**
	 * 银行结算账号
	 */
   	@Column(value = "Bank_acc" )
	private String bankAcc;

	/**
	 * IP 地址
	 */
   	@Column(value = "IP_address" )
	private String ipAddress;

	/**
	 * MAC 地址
	 */
   	@Column(value = "MAC_address" )
	private String macAddress;

	/**
	 * 国际移动设备
识别码
	 */
   	@Column(value = "IMEI" )
	private String imei;

	/**
	 * 通用唯一识别
码
	 */
   	@Column(value = "UUID" )
	private String uuid;

	/**
	 * 交易时间
	 */
   	@Column(value = "date_time" )
	private Date dateTime;

}
