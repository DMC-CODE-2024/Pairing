package com.eirs.pairs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ManualPairDto {

    private String imei;

    private String msisdn;

    private String guiMsisdn;

}
