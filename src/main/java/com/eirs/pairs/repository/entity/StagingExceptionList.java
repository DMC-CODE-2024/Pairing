package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "temp_exception_list")
public class StagingExceptionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actual_imei")
    private String actualImei;

    @Column(name = "imei")
    private String imei;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "operator_name")
    private String operatorName;

    private LocalDateTime createdOn;

    @Column(name = "edr_date_time")
    private LocalDateTime edrDatetime;

    @Column(name = "mode_type")
    private String modeType;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "msisdn")
    private String msisdn;

}
