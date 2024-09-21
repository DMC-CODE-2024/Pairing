package com.eirs.pairs.service;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.repository.ConfigRepository;
import com.eirs.pairs.repository.entity.SysParam;
import com.eirs.pairs.repository.entity.SystemConfigKeys;
import com.eirs.pairs.utils.DateFormatterConstants;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;

    Set<String> deviceTypes = new HashSet<>();

    LocalTime notificationSmsStartTime;

    LocalTime notificationSmsEndTime;

    LocalDate gracePeriodEndDate;

    Integer pairingAllowedDays;

    Integer pairingAllowedCount;

    Integer msisdnMinLength;

    Integer msisdnMaxLength;

    Integer maxOtpValidRetries;

    private NotificationLanguage defaultLanguage;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Autowired
    AppConfig appConfig;

    @PostConstruct
    public void init() {
        getAllowedDeviceTypes();
        getDefaultLanguage();
        getPairingAllowCount();
        getNotificationSmsEndTime();
        getNotificationSmsStartTime();
        getPairingAllowDays();
    }

    public Set<String> getAllowedDeviceTypes() throws RuntimeException {
        String key = SystemConfigKeys.valid_pairing_device_type;
        if (CollectionUtils.isEmpty(deviceTypes)) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (CollectionUtils.isEmpty(values)) {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Missing Key in Sys Param " + SystemConfigKeys.valid_pairing_device_type);
            } else {
                deviceTypes.addAll(Arrays.asList(values.get(0).getConfigValue().split(",")));
            }
        }
        return deviceTypes;
    }

    @Override
    public NotificationLanguage getDefaultLanguage() {
        if (defaultLanguage == null) {
            List<SysParam> values = repository.findByConfigKey(SystemConfigKeys.default_language);
            if (CollectionUtils.isEmpty(values)) {
                defaultLanguage = NotificationLanguage.en;
            } else {
                defaultLanguage = NotificationLanguage.valueOf(values.get(0).getConfigValue());
            }
        }
        return defaultLanguage;
    }

    @Override
    public LocalTime getNotificationSmsStartTime() {
        String key = SystemConfigKeys.notification_sms_start_time;
        List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
        if (notificationSmsStartTime == null) {
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsStartTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsStartTime;
    }


    @Override
    public LocalTime getNotificationSmsEndTime() {
        String key = SystemConfigKeys.notification_sms_end_time;
        List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
        if (notificationSmsEndTime == null) {
            if (!CollectionUtils.isEmpty(values)) {
                String value = values.get(0).getConfigValue();
                try {
                    String data[] = value.split(":");
                    notificationSmsEndTime = LocalTime.of(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                log.error("Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return notificationSmsEndTime;
    }

    @Override
    public LocalDate getGracePeriodEndDate() {
        String key = SystemConfigKeys.GRACE_PERIOD_END_DATE;
        if (gracePeriodEndDate == null) {
            List<SysParam> values = repository.findByConfigKey(key);
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    gracePeriodEndDate = LocalDate.parse(values.get(0).getConfigValue(), DateFormatterConstants.gracePeriodEndDateFormat);
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, "");
                    log.error("Error while getting Configuration missing in Sys_param table key:{} ", key, e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, "");
                log.error("Configuration missing in Sys_param table key:{} ", key);
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return gracePeriodEndDate;
    }

    @Override
    public Integer getPairingAllowDays() {
        String key = SystemConfigKeys.pairing_allowed_days;
        if (pairingAllowedDays == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    pairingAllowedDays = Integer.parseInt(values.get(0).getConfigValue());
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return pairingAllowedDays;
    }

    @Override
    public Integer getPairingAllowCount() {
        String key = SystemConfigKeys.pairing_allowed_count;
        if (pairingAllowedCount == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    pairingAllowedCount = Integer.parseInt(values.get(0).getConfigValue());
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return pairingAllowedCount;
    }

    @Override
    public Integer getMsisdnMinLength() {
        String key = SystemConfigKeys.msisdn_min_length;
        if (msisdnMinLength == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    msisdnMinLength = Integer.parseInt(values.get(0).getConfigValue());
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return msisdnMinLength;
    }

    @Override
    public Integer getMsisdnMaxLength() {
        String key = SystemConfigKeys.msisdn_max_length;
        if (msisdnMaxLength == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    msisdnMaxLength = Integer.parseInt(values.get(0).getConfigValue());
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return msisdnMaxLength;
    }

    @Override
    public Integer getMaxOtpValidRetries() {
        String key = SystemConfigKeys.pairing_otp_max_valid_retries;
        if (maxOtpValidRetries == null) {
            List<SysParam> values = repository.findByConfigKeyAndModule(key, appConfig.getModuleName());
            if (!CollectionUtils.isEmpty(values)) {
                try {
                    maxOtpValidRetries = Integer.parseInt(values.get(0).getConfigValue());
                } catch (Exception e) {
                    moduleAlertService.sendConfigurationWrongValueAlert(key, values.get(0).configValue, appConfig.getModuleName());
                    log.error("Error while getting Configuration missing in Sys_param table key:{} featureName:{}", key, appConfig.getModuleName(), e.getMessage());
                    throw new RuntimeException("Error for Configuration key " + key);
                }
            } else {
                moduleAlertService.sendConfigurationMissingAlert(key, appConfig.getModuleName());
                throw new RuntimeException("Configuration missing in sys_param for key " + key);
            }
        }
        return maxOtpValidRetries;
    }

}
