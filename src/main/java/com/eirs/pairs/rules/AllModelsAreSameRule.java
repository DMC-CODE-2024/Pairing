package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.service.ExceptionListService;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AllModelsAreSameRule")
public class AllModelsAreSameRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDtoTemp, ValidateOtpRequestDto validateOtpRequestDto) {
        Boolean result = true;
        String lastModel = validateOtpRequestDto.getPairs().get(0).getModel();
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            if (StringUtils.isBlank(pairDto.getModel())) {
                result = false;
                break;
            }
            if (!StringUtils.equals(lastModel, pairDto.getModel())) {
                result = false;
                break;
            }
        }
        log.info("Matching Models result:{} validateOtpRequestDto:{}", result, validateOtpRequestDto);
        if (!result) {
            for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_FAIL_DEVICE_MODELS_ARE_NOT_SAME, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_FAIL_DEVICE_MODELS_ARE_NOT_SAME);
                pairDto.setStatus(httpResp);
                pairDto.setDescription(description.getMsg());
            }
        }
        return result;
    }
}
