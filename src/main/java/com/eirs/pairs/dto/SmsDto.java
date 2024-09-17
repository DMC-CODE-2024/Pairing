package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsTag;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class SmsDto {

    private SmsTag tag;

    private String msg;

    private NotificationLanguage language;

    public String module;
}
