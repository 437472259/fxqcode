package com.zzb.zhenjvan.entity;


import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @Description
 * @Author  Hunter
 * @Date 2022-05-06
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table( value ="white_ip_mac" )
public class WhiteIpMac   {


   	@Column(value = "id" )
	private Long id;

	/**
	 * 设备名称
	 */
   	@Column(value = "deviceName" )
	private String deviceName;

	/**
	 * IP地址
	 */
   	@Column(value = "ip" )
	private String ip;

	/**
	 * MAC地址
	 */
   	@Column(value = "mac" )
	private String mac;

}
