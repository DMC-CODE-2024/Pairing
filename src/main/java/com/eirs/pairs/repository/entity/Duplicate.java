package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "duplicate_device_detail", catalog = "app")
public class Duplicate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "imei")
    private String imei;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "edr_time")
    private LocalDateTime edrTime;

    @Column(name = "operator")
    private String operator;

    @Column(name = "status")
    private String status;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "remark")
    private String remarks;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "document_type1")
    private String document_type1;

    @Column(name = "document_type2")
    private String document_type2;

    @Column(name = "document_type3")
    private String document_type3;

    @Column(name = "document_type4")
    private String document_type4;

    @Column(name = "document_path1")
    private String document_path1;

    @Column(name = "document_path2")
    private String document_path2;

    @Column(name = "document_path3")
    private String document_path3;

    @Column(name = "document_path4")
    private String document_path4;
}
