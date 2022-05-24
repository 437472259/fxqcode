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
@Table( value ="tb_product_acc" )
public class TbProductAcc {


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
	 *  产品全称
	 */
   	@Column(value = "Prod_full_name" )
	private String prodFullName;

	/**
	 * 产品简称
	 */
   	@Column(value = "Prod_name" )
	private String prodName;

	/**
	 * 产品代码
	 */
   	@Column(value = "Prod_code" )
	private String prodCode;

	/**
	 * 产品到期日期
	 */
   	@Column(value = "Prod_deadline" )
	private Date prodDeadline;

	/**
	 * 合同约定规模
	 */
   	@Column(value = "Prod_scale1" )
	private Double prodScale1;

	/**
	 * 资产管理人名
称
	 */
   	@Column(value = "Cst_name" )
	private String cstName;

	/**
	 * 资产管理人证
件类型 21 营业执照（含社会统
一信用代码证，多证合
一）； 22 其他。填写数
字。
	 */
   	@Column(value = "Id_type" )
	private String idType;

	/**
	 * 资产管理人证
件号码
	 */
   	@Column(value = "Id_no" )
	private String idNo;

	/**
	 * 资产管理人证
件有效期限到
期日
	 */
   	@Column(value = "Id_deadline" )
	private Date idDeadline;

	/**
	 * 投资者类型
	 */
   	@Column(value = "Industry" )
	private String industry;

	/**
	 * 经营范围/业
务范围
	 */
   	@Column(value = "Operate" )
	private String operate;

	/**
	 * 注册地址
	 */
   	@Column(value = "Address" )
	private String address;

	/**
	 * 注册资本
	 */
   	@Column(value = "Reg_amt" )
	private Double regAmt;

	/**
	 * 注册资本币种
	 */
   	@Column(value = "Cur" )
	private String cur;

	/**
	 * 法定代表人或
负责人姓名
	 */
   	@Column(value = "Rep_name" )
	private String repName;

	/**
	 * 定代表人或
负责人身份证件类型
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
限到期日
	 */
   	@Column(value = "Id_deadline3" )
	private String idDeadline3;

	/**
	 * 授权办理业务人员姓名
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
	 * 系统名称
	 */
   	@Column(value = "Sys_name" )
	private String sysName;

}
