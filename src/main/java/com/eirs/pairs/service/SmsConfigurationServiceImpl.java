package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.repository.SmsConfigurationEntityRepository;
import com.eirs.pairs.repository.entity.SmsConfigurationEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsConfigurationServiceImpl implements SmsConfigurationService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SmsConfigurationEntityRepository smsConfigurationEntityRepository;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Override
    public SmsDto getSms(SmsTag tag, NotificationLanguage language, String moduleName) {
        SmsDto retVal = null;
        try {
            if (language == null)
                language = systemConfigurationService.getDefaultLanguage();
            SmsConfigurationEntity smsConfiguration = smsConfigurationEntityRepository.findByTagAndLanguageAndModule(tag, language, moduleName);
            if (smsConfiguration == null) {
                retVal = new SmsDto();
                retVal.setMsg(tag.getDescription());
                retVal.setLanguage(language);
                retVal.setTag(tag);
                retVal.setModule("Default");
                log.info("Default SMS[{}] as SMS not found from eirs_response_param table for tag:{} language:{}", retVal, tag, language);
            } else {
                retVal = new SmsDto();
                retVal.setMsg(smsConfiguration.getMsg());
                retVal.setLanguage(smsConfiguration.getLanguage());
                retVal.setTag(smsConfiguration.getTag());
                retVal.setModule(smsConfiguration.getModule());
                log.info("SMS[{}] found from eirs_response_param table for tag:{} language:{}", retVal, tag, language);
            }
        } catch (Exception e) {
            moduleAlertService.sendSmsConfigMissingAlert(tag.name(), moduleName, language.name());
            retVal = new SmsDto();
            retVal.setMsg(tag.getDescription());
            retVal.setLanguage(language);
            retVal.setTag(tag);
            retVal.setModule("Default");
            log.error("Default SMS[{}] as Error while getting from eirs_response_param table for tag:{} language:{} Error:{}", retVal, tag, language, e.getMessage());
        }
        return retVal;
    }

    @Override
    public SmsDto getSms(SmsTag tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName) {
        SmsDto sms = getSms(tag, language, moduleName);
        return getMsg(smsPlaceHolder, sms);
    }

    private SmsDto getMsg(Map<SmsPlaceHolders, String> smsPlaceHolder, SmsDto msg) {
        SmsDto finalMsg = msg;
        if (smsPlaceHolder != null)
            for (SmsPlaceHolders key : smsPlaceHolder.keySet())
                finalMsg.setMsg(StringUtils.replaceIgnoreCase(finalMsg.getMsg(), key.getPlaceHolder(), smsPlaceHolder.get(key)));
        return finalMsg;
    }
}
