package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exception_list")
public class ExceptionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actualImei;

    private String tac;

    private String imei;

    private String imsi;

    private String msisdn;

    private String operatorId;

    private String operatorName;

    private LocalDateTime createdOn;

    private String source;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "txn_id")
    private String txnId;
}
