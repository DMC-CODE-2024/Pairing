package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.service.ExceptionListService;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("GsmaValidRule")
public class GsmaValidRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDtoTemp, ValidateOtpRequestDto validateOtpRequestDto) {
        Boolean result = true;
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            if (StringUtils.isBlank(pairDto.getModel()) || StringUtils.isBlank(pairDto.getDeviceType())) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_FAIL_GSMA_INVALID, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_FAIL_GSMA_INVALID);
                pairDto.setStatus(httpResp);
                pairDto.setDescription(description.getMsg());
                result = false;
            }
        }
        log.info("GSMA Validation Check result:{} validateOtpRequestDto:{}", result, validateOtpRequestDto);
        return result;
    }
}
