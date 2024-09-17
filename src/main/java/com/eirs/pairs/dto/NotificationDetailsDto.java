package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NotificationDetailsDto {

    private String msisdn;
    private String emailId;
    private Map<SmsPlaceHolders, String> smsPlaceHolder;
    private SmsTag smsTag;
    private SmsTag subjectSmsTag;
    private NotificationLanguage language;
    private String moduleName;
    private String requestId;

}
