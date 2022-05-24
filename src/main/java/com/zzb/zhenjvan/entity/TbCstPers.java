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
@Table( value ="tb_cst_pers" )
public class TbCstPers  {

	/**
	 * 分支机构
	 */
   	@Column(value = "futures_code1" )
	private String futuresCode1;

	/**
	 * 金融机构编码
	 */
   	@Column(value = "futures_code2" )
	private String futuresCode2;

	/**
	 * 资金账号
	 */
   	@Column(value = "self_acc_no" )
	private String selfAccNo;

	/**
	 * 创建日期
	 */
   	@Column(value = "open_time" )
	private Date openTime;

	/**
	 * 结束日期
	 */
   	@Column(value = "close_time" )
	private Date closeTime;

	/**
	 * 客户名称
	 */
   	@Column(value = "cst_name" )
	private String cstName;

	/**
	 * 客户性别 11 男，
12 女，用数字代表
	 */
   	@Column(value = "cst_sex" )
	private String cstSex;

	/**
	 * 国籍（地区）
	 */
   	@Column(value = "nation" )
	private String nation;

	/**
	 * 证件类别 11 居民
身份证或临时身份证；
12 军人或武警身份证
件； 13 港澳居民来往
内地通行证，台湾居民
来往大陆通行证或其
他有效旅行证件； 14
港澳台居民居住证；15
外国公民护照； 16 户
口簿； 17 出生证； 18
其他类个人身份证件。
填写数字。
	 */
   	@Column(value = "id_type" )
	private String idType;

	/**
	 * 身份证件号码
	 */
   	@Column(value = "id_no" )
	private String idNo;

	/**
	 * 身份证件有效
期限到期日
	 */
   	@Column(value = "id_deadline" )
	private String idDeadline;

	/**
	 * 职业代码
	 */
   	@Column(value = "occupation_code" )
	private String occupationCode;

	/**
	 * 职业
	 */
   	@Column(value = "occupation" )
	private String occupation;

	/**
	 * 年收入
	 */
   	@Column(value = "income" )
	private Double income;

	/**
	 * 联系方式
	 */
   	@Column(value = "contact1" )
	private String contact1;

	/**
	 * 其他联系方式
	 */
   	@Column(value = "contact2" )
	private String contact2;

	/**
	 * 其他联系方式
	 */
   	@Column(value = "contact3" )
	private String contact3;

	/**
	 * 住所地或工作
单位地址
	 */
   	@Column(value = "address1" )
	private String address1;

	/**
	 * 其他住所地或
工作单位地址
	 */
   	@Column(value = "address2" )
	private String address2;

	/**
	 * 其他住所地或
工作单位地址
	 */
   	@Column(value = "address3" )
	private String address3;

	/**
	 * 系统名称
	 */
   	@Column(value = "sys_name" )
	private String sysName;

}
