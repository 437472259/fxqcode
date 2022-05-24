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
@Table( value ="tb_cst_unit" )
public class TbCstUnit {


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
	 * 创建日期
	 */
   	@Column(value = "Open_time" )
	private Date openTime;

	/**
	 * 结束日期
	 */
   	@Column(value = "Close_time" )
	private Date closeTime;

	/**
	 * 客户名称
	 */
   	@Column(value = "Cst_name" )
	private String cstName;

	/**
	 * 住所地
	 */
   	@Column(value = "Address" )
	private String address;

	/**
	 * 经营范围/业
务范围
	 */
   	@Column(value = "Operate" )
	private String operate;

	/**
	 * 单位性质
	 */
   	@Column(value = "State_owned_prop" )
	private String stateOwnedProp;

	/**
	 * 依法设立或经
营的执照名称 21 营业执照（含社会统
一信用代码证，多证合
一）； 22 其他。填写数
字。
	 */
   	@Column(value = "Set_file" )
	private String setFile;

	/**
	 * 依法设立或经
营的执照号码
	 */
   	@Column(value = "License" )
	private String license;

	/**
	 * 依法设立或经
营的执照有效
期限到期日
	 */
   	@Column(value = "Id_deadline" )
	private String idDeadline;

	/**
	 * 组织机构代码
	 */
   	@Column(value = "Org_no" )
	private String orgNo;

	/**
	 * 税务登记证号
码
	 */
   	@Column(value = "Tax_no" )
	private String taxNo;

	/**
	 * 法定代表人或
负责人姓名
	 */
   	@Column(value = "Rep_name" )
	private String repName;

	/**
	 * 法定代表人或
负责人身份证
件类型 11 居民身
份证或临时身份证； 12
军人或武警身份证件；
13 港澳居民来往内地
通行证，台湾居民来往
大陆通行证或其他有
效旅行证件；14 港澳台
居民居住证；15 外国公
民护照； 16 户口簿； 17
出生证；18 其他类个人身份证件
	 */
   	@Column(value = "Id_type2" )
	private String idType2;

	/**
	 * 法定代表人或
负责人身份证
件号码
	 */
   	@Column(value = "Id_no2" )
	private String idNo2;

	/**
	 * 法定代表人或
负责人身份证
件有效期限到
期日
	 */
   	@Column(value = "Id_deadline2" )
	private String idDeadline2;

	/**
	 * 控股股东或者
实际控制人姓
名
	 */
   	@Column(value = "Man_name" )
	private String manName;

	/**
	 * 控股股东或者
实际控制人身
份证件类型
	 */
   	@Column(value = "Id_type3" )
	private String idType3;

	/**
	 * 控股股东或者
实际控制人身
份证件号码
	 */
   	@Column(value = "Id_no3" )
	private String idNo3;

	/**
	 * 控股股东或者
实际控制人身
份证件有效期
	 */
   	@Column(value = "Id_deadline3" )
	private String idDeadline3;

	/**
	 * 授权办理业务
人员姓名
	 */
   	@Column(value = "Contact_name" )
	private String contactName;

	/**
	 * 授权办理业务
人员身份证件
类型
	 */
   	@Column(value = "Id_type4" )
	private String idType4;

	/**
	 * 授权办理业务
人员身份证件
号码
	 */
   	@Column(value = "Id_no4" )
	private String idNo4;

	/**
	 * 授权办理业务
人员身份证件
有效期限到期
日
	 */
   	@Column(value = "Id_deadline4" )
	private String idDeadline4;

	/**
	 * 投资者类型
	 */
   	@Column(value = "Industry" )
	private String industry;

	/**
	 * 注册资本金
	 */
   	@Column(value = "Reg_amt" )
	private BigDecimal regAmt;

	/**
	 * 注册资本金币
种
	 */
   	@Column(value = "Code" )
	private String code;

	/**
	 * 系统名称
	 */
   	@Column(value = "Sys_name" )
	private String sysName;

}
