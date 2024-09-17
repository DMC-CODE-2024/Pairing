package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grey_list")
public class GreyList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actualImei;

    private String imei;

    private String imsi;

    private String msisdn;

    private String operatorId;

    private String operatorName;

    private LocalDateTime createdOn;

    private String source;

}
