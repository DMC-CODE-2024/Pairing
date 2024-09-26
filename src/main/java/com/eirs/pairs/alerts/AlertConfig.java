package com.eirs.pairs.alerts;

import com.eirs.pairs.alerts.constants.AlertIds;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class AlertConfig {

    @Value("${eirs.alert.url}")
    private String url;

    private Map<AlertIds, AlertConfigDto> alertsMapping;

    @PostConstruct
    public void init() {
        alertsMapping = new HashMap<>();
        alertsMapping = new HashMap<>();
        alertsMapping.put(AlertIds.CONFIGURATION_VALUE_MISSING, new AlertConfigDto("alert2201"));
        alertsMapping.put(AlertIds.CONFIGURATION_VALUE_WRONG, new AlertConfigDto("alert2202"));
        alertsMapping.put(AlertIds.SMS_VALUE_MISSING, new AlertConfigDto("alert2203"));
        alertsMapping.put(AlertIds.MODULE_EXECUTED_WITH_EXCEPTION, new AlertConfigDto("alert2204"));
        alertsMapping.put(AlertIds.NOTIFICATION_SEND_EXCEPTION, new AlertConfigDto("alert2205"));
        alertsMapping.put(AlertIds.DATABASE_EXCEPTION, new AlertConfigDto("alert2206"));
        alertsMapping.put(AlertIds.DATABASE_TABLE_EXCEPTION, new AlertConfigDto("alert2206"));
    }
}
