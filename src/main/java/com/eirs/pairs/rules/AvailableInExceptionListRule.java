package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ExceptionListConstants;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.CustomRepository;
import com.eirs.pairs.repository.entity.CustomEntity;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.service.ExceptionListService;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInExceptionListRule")
public class AvailableInExceptionListRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    private ExceptionListService exceptionListService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {

        List<ExceptionList> exceptionLists = getByImeiAndImsiAndMsisdn(pairDto);
        if (CollectionUtils.isNotEmpty(exceptionLists)) {
            SmsDto description;
            String httpResp;
            if (ExceptionListConstants.VIP.name().equalsIgnoreCase(exceptionLists.get(0).getRequestType())) {
                description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_EXCEPTION_LIST_VIP_NO_PAIR_REQUIRED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_EXCEPTION_LIST_VIP_NO_PAIR_REQUIRED);
            } else {
                description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_EXCEPTION_LIST_NON_VIP_NO_PAIR_REQUIRED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_EXCEPTION_LIST_NON_VIP_NO_PAIR_REQUIRED);
            }
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
            return true;
        }
        pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
        return false;
    }

    private List<ExceptionList> getByImeiAndImsiAndMsisdn(PairDto pairDto) {
        List<ExceptionList> exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(pairDto.getImei(), null, null);
        if (CollectionUtils.isNotEmpty(exceptionLists)) {
            return exceptionLists;
        }
        if (pairDto.getImsi() != null) {
            exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(exceptionLists)) {
                return exceptionLists;
            }
        }
        exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(null, null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(exceptionLists)) {
            return exceptionLists;
        }

        if (pairDto.getImsi() != null) {
            exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(exceptionLists)) {
                return exceptionLists;
            }
        }

        exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(pairDto.getImei(), null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(exceptionLists)) {
            return exceptionLists;
        }

        if (pairDto.getImsi() != null) {
            exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(exceptionLists)) {
                return exceptionLists;
            }
        }

        if (pairDto.getImsi() != null) {
            exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(exceptionLists)) {
                return exceptionLists;
            }
        }

        return null;
    }
}
