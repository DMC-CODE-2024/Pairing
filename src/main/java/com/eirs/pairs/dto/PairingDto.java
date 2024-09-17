package com.eirs.pairs.dto;

import com.eirs.pairs.constants.GSMAStatus;
import com.eirs.pairs.constants.PairMode;
import com.eirs.pairs.constants.SyncStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PairingDto {

    private String imei;

    private String actualImei;

    private String imsi;

    private String msisdn;

    private LocalDateTime recordTime;

    private GSMAStatus gsmaStatus;

    private PairMode pairMode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime pairingDate;

    private int allowedDays;

    private String operator;

    private SyncStatus syncStatus;
}
