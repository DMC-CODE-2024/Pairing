package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.DuplicateRepository;
import com.eirs.pairs.repository.entity.Duplicate;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInDuplicateRule")
public class AvailableInDuplicateRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DuplicateRepository duplicateRepository;
    @Autowired
    SmsConfigurationService smsConfigurationService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        List<Duplicate> duplicates = duplicateRepository.findByImei(pairDto.getImei());
        log.info("Duplicate for IMEI:{} duplicates:{}", pairDto.getImei(), duplicates);
        if (CollectionUtils.isNotEmpty(duplicates)) {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_DUPLICATE_IMEI_FAIL, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_DUPLICATE_IMEI_FAIL);
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
            return true;
        } else {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
            return false;
        }
    }

}
