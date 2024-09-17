package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.DuplicateRepository;
import com.eirs.pairs.repository.NationalWhitelistRepository;
import com.eirs.pairs.repository.entity.Duplicate;
import com.eirs.pairs.repository.entity.NationalWhitelist;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInNationalWhitelistRule")
public class AvailableInNationalWhitelistRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NationalWhitelistRepository nationalWhitelistRepository;
    @Autowired
    SmsConfigurationService smsConfigurationService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        NationalWhitelist nationalWhitelist = nationalWhitelistRepository.findByImeiAndGdceImeiStatusGreaterThan(pairDto.getImei(), 0);
        log.info("NWL for IMEI:{} nationalWhitelist:{}", pairDto.getImei(), nationalWhitelist);
        if (nationalWhitelist == null) {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
            return false;
        } else {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_NWL_NO_PAIR_REQUIRED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_NWL_NO_PAIR_REQUIRED);
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
            return true;
        }
    }

}
