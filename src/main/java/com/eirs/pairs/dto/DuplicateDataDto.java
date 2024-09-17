package com.eirs.pairs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateDataDto {
    private String imei;
    private Set<String> imsi;
}
