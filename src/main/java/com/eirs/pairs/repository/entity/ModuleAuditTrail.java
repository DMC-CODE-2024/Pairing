package com.eirs.pairs.repository.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAuditTrail {

    private LocalDateTime createdOn;
    private Integer statusCode;
    private String featureName;
    private String moduleName;
    private Integer count;
    private Long timeTaken;
}
