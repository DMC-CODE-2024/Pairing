package com.eirs.pairs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PairDto {

    private String actualImei;

    private String imei;

    private String msisdn;

    private String guiMsisdn;

    private String imsi;

    private String model;

    private String deviceType;

    private String operator;

    private String status;

    private String description;

    public PairDto(String actualImei, String msisdn) {
        this.actualImei = actualImei;
        this.imei = actualImei.substring(0, 14);
        this.msisdn = msisdn;
    }

}
