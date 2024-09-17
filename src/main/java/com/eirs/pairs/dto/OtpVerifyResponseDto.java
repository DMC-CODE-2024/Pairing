package com.eirs.pairs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OtpVerifyResponseDto {

    private String response;

    private String description;

    private List<PairingDto> pairs;

    private List<PairDto> pairsStatus;
}
