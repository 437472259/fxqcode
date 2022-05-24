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
@Table( value ="tb_bnf_info" )
public class TbBnfInfo {


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
	 * 受益所有人姓
名
	 */
   	@Column(value = "Bnf_name" )
	private String bnfName;

	/**
	 * 受益所有人地
址
	 */
   	@Column(value = "Bnf_address" )
	private String bnfAddress;

	/**
	 * 判定受益所有
人方式 11： 股权或表决权； 12：
人事、财务控制； 13：
高级管理人； 14 其他
	 */
   	@Column(value = "Bnf_type" )
	private String bnfType;

	/**
	 * 持股数量或表
决权占比 单位： %。填写百分比，
保留两位小数，如
51.66，不填写“ %”符
号。 Bnf_type=11 时填
写，不等 11 时填写
“ @N”。
	 */
   	@Column(value = "Shareholding_ratio" )
	private String shareholdingRatio;

	/**
	 * 受益所有人证
件类型 11：居民身份证或临时
身份证； 12：军人或武
警身份证件； 13：港澳居民来往内地通行证，
台湾居民来往大陆通
行证或其他有效旅行
证件； 14、港澳台居民
居住证； 15：外国公民
护照； 18：其他类个人
身份证件。
填写数字。
	 */
   	@Column(value = "Id_type5" )
	private String idType5;

	/**
	 * 受益所有人证
件号码
	 */
   	@Column(value = "Id_no5" )
	private String idNo5;

	/**
	 * 受益所有人身
份证件有效期
限
	 */
   	@Column(value = "Id_deadline5" )
	private Date idDeadline5;

	/**
	 * 系统名称
	 */
   	@Column(value = "Sys_name" )
	private String sysName;

}
