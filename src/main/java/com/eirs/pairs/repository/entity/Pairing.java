package com.eirs.pairs.repository.entity;

import com.eirs.pairs.constants.GSMAStatus;
import com.eirs.pairs.constants.PairMode;
import com.eirs.pairs.constants.SyncStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "imei_pair_detail")
public class Pairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "imei")
    private String imei;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "actual_imei")
    private String actualImei;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Column(name = "gsma_status")
    @Enumerated(EnumType.STRING)
    private GSMAStatus gsmaStatus;

    @Column(name = "pair_mode")
    @Enumerated(EnumType.STRING)
    private PairMode pairMode;

    @Column(name = "pairing_date")
    private LocalDateTime pairingDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "allowed_days")
    private int allowedDays;

    @Column(name = "operator")
    private String operator;

    @Column(name = "txn_id")
    private String txnId;

}
