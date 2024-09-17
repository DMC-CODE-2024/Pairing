package com.eirs.pairs.utils.notification.dto;

import com.eirs.pairs.constants.NotificationChannelType;
import com.eirs.pairs.constants.NotificationLanguage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationRequestDto {

    private NotificationChannelType channelType;

    private String featureName;

    private String subFeature;

    private String featureTxnId;

    private String email;

    private NotificationLanguage msgLang;

    private String msisdn;

    private String message;

    private String operatorName;

    private String subject;

    private LocalDateTime deliveryDateTime;
}
