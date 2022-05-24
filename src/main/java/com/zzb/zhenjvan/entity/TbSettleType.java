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
@Table( value ="tb_settle_type" )
public class TbSettleType  {


   	@Column(value = "id" )
	private Long id;

	/**
	 * 法人机构报告机构编码
	 */
   	@Column(value = "head_no" )
	private String headNo;

	/**
	 * 金融机构编码
	 */
   	@Column(value = "futures_code2" )
	private String futuresCode2;

	/**
	 * 业务标志
	 */
   	@Column(value = "settle_type" )
	private String settleType;

	/**
	 * 业务名称
	 */
   	@Column(value = "name" )
	private String name;

}
