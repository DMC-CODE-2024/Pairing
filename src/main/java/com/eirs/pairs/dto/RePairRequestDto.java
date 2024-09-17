package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RePairRequestDto {

    private String imei;

    private String oldMsisdn;

    private String newMsisdn;

    private String guiNewMsisdn;

    private String serialNumber;

    private NotificationLanguage language;

}
