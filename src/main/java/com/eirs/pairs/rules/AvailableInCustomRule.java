package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.CustomRepository;
import com.eirs.pairs.repository.DuplicateRepository;
import com.eirs.pairs.repository.entity.CustomEntity;
import com.eirs.pairs.repository.entity.Duplicate;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInCustomRule")
public class AvailableInCustomRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    private CustomRepository customRepository;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        Boolean isCustomPaid = false;
        CustomEntity customImei = customRepository.findByImei(pairDto.getImei());
        log.info("Custom Details for IMEI:{} customImei:{}", pairDto.getImei(), customImei);
        boolean isAvailable = (customImei == null ? false : (customImei.getIsCustomTaxPaid() == 1 ? true : false));
        if (isAvailable) {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_OTP_CUSTOM_CHECKED_NO_PAIR_REQUIRED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_OTP_CUSTOM_CHECKED_NO_PAIR_REQUIRED);
            isCustomPaid = true;
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
        } else {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
        }
        return isCustomPaid;
    }

}
