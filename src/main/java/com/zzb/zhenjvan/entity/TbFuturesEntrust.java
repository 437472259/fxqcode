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
@Table( value ="tb_futures_entrust" )
public class TbFuturesEntrust  {


   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

   	@Column(value = "Cst_name" )
	private String cstName;

   	@Column(value = "Contract_code" )
	private String contractCode;

   	@Column(value = "Contract_name" )
	private String contractName;

   	@Column(value = "Entrust_no" )
	private String entrustNo;

   	@Column(value = "Date" )
	private Date date;

   	@Column(value = "Time" )
	private String time;

   	@Column(value = "Entrust_flag" )
	private String entrustFlag;

   	@Column(value = "Entrust_prop" )
	private String entrustProp;

   	@Column(value = "Entrust_amount" )
	private String entrustAmount;

   	@Column(value = "Entrust_price" )
	private String entrustPrice;

   	@Column(value = "Entrust_amt" )
	private String entrustAmt;

   	@Column(value = "Lend_flag" )
	private String lendFlag;

   	@Column(value = "Futures_direction" )
	private String futuresDirection;

   	@Column(value = "entrust_status" )
	private String entrustStatus;

   	@Column(value = "hedge_type" )
	private String hedgeType;

   	@Column(value = "IP_address" )
	private String ipAddress;

   	@Column(value = "MAC_address" )
	private String macAddress;

   	@Column(value = "IMEI" )
	private String imei;

   	@Column(value = "UUID" )
	private String uuid;

	/**
	 * 交易时间
	 */
   	@Column(value = "date_time" )
	private Date dateTime;

}
