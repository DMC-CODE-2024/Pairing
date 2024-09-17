package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_trail", catalog = "aud")
public class GuiAuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdOn;

    private LocalDateTime modifiedOn;

    private Integer userId;
    private String userName;
    private Integer userTypeId;
    private String userType;
    private Integer featureId;
    private String featureName;
    private String subFeature;
    private String jSessionId;
    private String roleType;
    private String publicIp;
    private String browser;
    private String details;
    private String txnId;


}
