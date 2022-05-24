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
@Table( value ="tb_sus_report" )
public class TbSusReport {


   	@Column(value = "RICD" )
	private String ricd;

   	@Column(value = "FINC" )
	private String finc;

   	@Column(value = "SENM" )
	private String senm;

   	@Column(value = "SETN" )
	private String setn;

   	@Column(value = "STNM" )
	private String stnm;

   	@Column(value = "SETP" )
	private String setp;

   	@Column(value = "SEID" )
	private String seid;

   	@Column(value = "SCAC" )
	private String scac;

   	@Column(value = "FDAC" )
	private String fdac;

   	@Column(value = "STAC" )
	private String stac;

   	@Column(value = "SFIN" )
	private String sfin;

   	@Column(value = "TAAC" )
	private String taac;

   	@Column(value = "SEVC" )
	private String sevc;

   	@Column(value = "SCTL" )
	private String sctl;

   	@Column(value = "SEEI" )
	private String seei;

   	@Column(value = "SRNM" )
	private String srnm;

   	@Column(value = "SCIT" )
	private String scit;

   	@Column(value = "ORIT" )
	private String orit;

   	@Column(value = "SRID" )
	private String srid;

   	@Column(value = "SCNM" )
	private String scnm;

   	@Column(value = "SCIT1" )
	private String scit1;

   	@Column(value = "OCIT" )
	private String ocit;

   	@Column(value = "SCID" )
	private String scid;

   	@Column(value = "STNT" )
	private String stnt;

   	@Column(value = "OATM" )
	private String oatm;

   	@Column(value = "CATM" )
	private String catm;

   	@Column(value = "DETR" )
	private String detr;

   	@Column(value = "TORP" )
	private String torp;

   	@Column(value = "DORP" )
	private String dorp;

   	@Column(value = "ODRP" )
	private String odrp;

   	@Column(value = "TPTR" )
	private String tptr;

   	@Column(value = "OTPR" )
	private String otpr;

   	@Column(value = "STCB" )
	private String stcb;

   	@Column(value = "AOSP" )
	private String aosp;

   	@Column(value = "TOSC" )
	private String tosc;

   	@Column(value = "STCR" )
	private String stcr;

   	@Column(value = "CTNM" )
	private String ctnm;

   	@Column(value = "CITP" )
	private String citp;

   	@Column(value = "OITP" )
	private String oitp;

   	@Column(value = "CTID" )
	private String ctid;

   	@Column(value = "TSTM" )
	private String tstm;

   	@Column(value = "TICD" )
	private String ticd;

   	@Column(value = "OCTT" )
	private String octt;

   	@Column(value = "OOCT" )
	private String ooct;

   	@Column(value = "OCEC" )
	private String ocec;

   	@Column(value = "TOTS" )
	private String tots;

   	@Column(value = "CTNO" )
	private String ctno;

   	@Column(value = "SRNO" )
	private String srno;

   	@Column(value = "TTCD" )
	private String ttcd;

   	@Column(value = "TRPR" )
	private String trpr;

   	@Column(value = "TVOL" )
	private String tvol;

   	@Column(value = "CRDR" )
	private String crdr;

   	@Column(value = "CSTP" )
	private String cstp;

   	@Column(value = "CRTP" )
	private String crtp;

   	@Column(value = "CRAT" )
	private String crat;

   	@Column(value = "ROTF" )
	private String rotf;

}
