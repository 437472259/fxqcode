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
@Table( value ="tb_risk_his" )
public class TbRiskHis {


   	@Column(value = "Futures_code1" )
	private String futuresCode1;

   	@Column(value = "Futures_code2" )
	private String futuresCode2;

   	@Column(value = "SELF_ACC_NO" )
	private String selfAccNo;

   	@Column(value = "CST_NAME" )
	private String cstName;

   	@Column(value = "ID_NO" )
	private String idNo;

   	@Column(value = "ACC_TYPE" )
	private String accType;

   	@Column(value = "RISK_CODE" )
	private String riskCode;

   	@Column(value = "TIME" )
	private String time;

   	@Column(value = "FIRST_TYPE" )
	private String firstType;

   	@Column(value = "SCORE" )
	private String score;

   	@Column(value = "NORM" )
	private String norm;

	/**
	 * 风险等级评定时间
 	 */
	private Date riskDate;

}
