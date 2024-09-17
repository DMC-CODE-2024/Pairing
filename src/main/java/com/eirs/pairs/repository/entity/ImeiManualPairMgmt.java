package com.eirs.pairs.repository.entity;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.PairRequestTypes;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "imei_manual_pair_mgmt", catalog = "app")
public class ImeiManualPairMgmt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "imei1")
    private String imei1;

    @Column(name = "imei2")
    private String imei2;

    @Column(name = "imei3")
    private String imei3;

    @Column(name = "imei4")
    private String imei4;

    @Column(name = "msisdn1")
    private String msisdn1;

    @Column(name = "msisdn2")
    private String msisdn2;

    @Column(name = "msisdn3")
    private String msisdn3;

    @Column(name = "msisdn4")
    private String msisdn4;

    @Column(name = "gui_msisdn1")
    private String guiMsisdn1;

    @Column(name = "gui_msisdn2")
    private String guiMsisdn2;

    @Column(name = "gui_msisdn3")
    private String guiMsisdn3;

    @Column(name = "gui_msisdn4")
    private String guiMsisdn4;

    @Column(name = "old_msisdn")
    private String oldMsisdn;

    @Column(name = "otp")
    private Integer otp;

    @Column(name = "contact_number_otp")
    private String contactNumber;

    @Column(name = "email_id_otp")
    private String emailId;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "fail_reason")
    private String failReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private PairRequestTypes requestType;

    @Column(name = "otp_retries")
    private Integer otpRetriesCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private NotificationLanguage language;


    @Column(name = "status1")
    private String status1;

    @Column(name = "status2")
    private String status2;

    @Column(name = "status3")
    private String status3;

    @Column(name = "status4")
    private String status4;

    @Column(name = "description1")
    private String description1;

    @Column(name = "description2")
    private String description2;

    @Column(name = "description3")
    private String description3;

    @Column(name = "description4")
    private String description4;
}
