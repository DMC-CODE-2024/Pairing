package com.eirs.pairs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ReSendOtpRequestDto {

    private String requestId;

}
