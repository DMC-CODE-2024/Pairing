package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PairStatusRequestDto {

    private String imei;

    private String msisdn;

    private String serialNumber;

    private String contactNumber;

    private String emailId;

    private NotificationLanguage language;
}
