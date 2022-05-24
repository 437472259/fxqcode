package com.zzb.zhenjvan.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author flynn
 * @date 2022/5/14 14:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    private Integer id;

    private Date txDt;

    private String userId;

    private String loginSeq;

    private String investorId;

    private Date loginTime;

    private String ip;

    private String mac;

    private String dridentityid;

    private String userproduct;

    private String srcIp;

    private String srcMac;

    private String frontId;

    private String sessionId;

    private String appId;

    private String origDepartmentId;
}