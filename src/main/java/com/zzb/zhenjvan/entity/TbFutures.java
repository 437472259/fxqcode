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
@Table( value ="tb_futures" )
public class TbFutures {


   	@Column(value = "id" )
	private Long id;

	/**
	 * 法人机构报告机构编码
	 */
   	@Column(value = "head_no" )
	private String headNo;

	/**
	 * 机构网点代码
	 */
   	@Column(value = "futures_code1" )
	private String futuresCode1;

	/**
	 * 金融机构编码
	 */
   	@Column(value = "futures_code2" )
	private String futuresCode2;

	/**
	 * 机构名称
	 */
   	@Column(value = "futures_name" )
	private String futuresName;

	/**
	 * 跨境标识 10 境内； 11 境外
	 */
   	@Column(value = "bord_type" )
	private String bordType;

}
