package com.zzb.zhenjvan.entity;



import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table( value ="tb_acc" )
public class TbAcc   {


	/**
	 * 分支机构
	 */
   	@Column(value = "Futures_code1" )
	private String futuresCode1;

	/**
	 * 金融机构编码
	 */
   	@Column(value = "Futures_code2" )
	private String futuresCode2;

	/**
	 * 客户名称
	 */
   	@Column(value = "Cst_name" )
	private String cstvalue;

	/**
	 * 证件号码
	 */
   	@Column(value = "Id_no" )
	private String idNo;

	/**
	 * 账户状态11 正常， 12 休眠， 13
销户， 14 其它
	 */
   	@Column(value = "Acc_state" )
	private String accState;

	/**
	 * 限制措施
	 */
   	@Column(value = "Restr_mea" )
	private String restrMea;

	/**
	 * 资金账号
	 */
   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

	/**
	 * 公私标识
	 */
   	@Column(value = "Acc_type" )
	private String accType;

	/**
	 * 开户日期
	 */
   	@Column(value = "Open_time" )
	private Date openTime;

	/**
	 * 销户日期
	 */
   	@Column(value = "Close_time" )
	private String closeTime;

	/**
	 * 开户方式 11 柜面， 12 非柜面
	 */
   	@Column(value = "Open_type" )
	private String openType;

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

}
