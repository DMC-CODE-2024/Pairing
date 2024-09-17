package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "national_whitelist", catalog = "app")
public class NationalWhitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "national_whitelist_id")
    private Long id;

    @Column(name = "imei")
    private String imei;

    @Column(name = "gdce_imei_status")
    private Integer gdceImeiStatus;

}