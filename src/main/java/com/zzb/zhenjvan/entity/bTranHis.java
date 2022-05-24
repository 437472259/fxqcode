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
@Table( value ="tb_tran_his" )
public class bTranHis {


   	@Column(value = "Futures_code1" )
	private String futuresCode1;

   	@Column(value = "Futures_code2" )
	private String futuresCode2;

   	@Column(value = "Self_acc_no" )
	private String selfAccNo;

   	@Column(value = "Cst_name" )
	private String cstName;

   	@Column(value = "Acc_type" )
	private String accType;

   	@Column(value = "Id_type" )
	private String idType;

   	@Column(value = "Id_no" )
	private String idNo;

   	@Column(value = "Trans_no" )
	private String transNo;

   	@Column(value = "Trans_date" )
	private String transDate;

   	@Column(value = "Content" )
	private String content;

   	@Column(value = "Purpose" )
	private String purpose;

}
