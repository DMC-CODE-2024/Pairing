package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.service.ExceptionListService;
import com.eirs.pairs.service.SmsConfigurationService;
import com.eirs.pairs.service.SystemConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service("AllowedDeviceTypeRule")
public class AllowedDeviceTypeRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    private ExceptionListService exceptionListService;

    @Autowired
    SystemConfigurationService systemConfigurationService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        Boolean isAllowedDeviceType = true;
        Set<String> allowedDeviceTypes = systemConfigurationService.getAllowedDeviceTypes();
            Optional<String> find = allowedDeviceTypes.stream().filter(deviceType -> StringUtils.equalsIgnoreCase(deviceType, pairDto.getDeviceType())).findAny();
        boolean contains = find.isPresent() ? true : false;
        log.info("Allowed Device Type contains:{} allowedDeviceTypes:{} validateOtpRequestDto:{}", contains, allowedDeviceTypes, validateOtpRequestDto);
        if (!contains) {
            isAllowedDeviceType = contains;
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_FAIL_DEVICE_TYPES_ARE_NOT_ALLOWED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_FAIL_DEVICE_TYPES_ARE_NOT_ALLOWED);
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
        }
        return isAllowedDeviceType;
    }

}
