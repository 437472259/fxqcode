package com.zzb.zhenjvan.entity;


import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
@Table( value ="tb_risk_new" )
public class TbRiskNew {

   	@Column(value = "FUTURES_CODE1" )
	private String futuresCode1;

   	@Column(value = "FUTURES_CODE2" )
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

   	@Column(value = "SCORE" )
	private String score;

   	@Column(value = "NORM" )
	private String norm;

}
