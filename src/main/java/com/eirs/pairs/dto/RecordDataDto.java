package com.eirs.pairs.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordDataDto {

    private LocalDateTime date;
    private String actualImei;
    private String imei;
    private String imsi;

    private String msisdn;

    private String operatorId;
    private String operatorName;
    private String filename;

    private String deviceType;
    private Boolean isGsmaValid;
    private String txnId;

    private Boolean isCustomPaid; //3=amnisty and allowed, 0 invalid other than zero is valid.

}
