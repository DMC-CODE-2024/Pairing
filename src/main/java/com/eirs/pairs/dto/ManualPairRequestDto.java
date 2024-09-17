package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ManualPairRequestDto {

    private List<ManualPairDto> pairs;

    private String serialNumber;

    private String contactNumber;

    private String emailId;

    private NotificationLanguage language;

}
