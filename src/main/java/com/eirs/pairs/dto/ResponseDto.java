package com.eirs.pairs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private String status;

    private ErrorInfo errorInfo;

    private Object result;
}
