package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.BlackListRepository;
import com.eirs.pairs.repository.GreyListRepository;
import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.GreyList;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInGreyListRule")
public class AvailableInGreyListRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    private GreyListRepository greyListRepository;

    private String SOURCES[] = new String[]{"LOST", "STOLEN"};
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        List<GreyList> greyLists = getByImeiAndImsiAndMsisdn(pairDto);
        log.info("Found GreyList for IMEI:{} greyLists:{}", pairDto.getImei(), greyLists);
        if (CollectionUtils.isNotEmpty(greyLists)) {
            boolean isSourceAvailable = sourceValidation(greyLists);
            if (isSourceAvailable) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_GREYLIST_IMEI_FAIL, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_GREYLIST_IMEI_FAIL);
                pairDto.setStatus(httpResp);
                pairDto.setDescription(description.getMsg());
                log.info("Grey List Imei found SOURCES:{} for pairDto:{}", SOURCES, pairDto);
            }
            return isSourceAvailable;
        } else {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
            return false;
        }
    }

    private boolean sourceValidation(List<GreyList> greyLists) {
        for (GreyList greyList : greyLists) {
            if (StringUtils.isNotBlank(greyList.getSource())) {
                String sources[] = greyList.getSource().split(",");
                for (String source : sources)
                    if (StringUtils.equalsAnyIgnoreCase(source, SOURCES)) {
                        return true;
                    }
            }
        }
        return false;
    }

    private List<GreyList> getByImeiAndImsiAndMsisdn(PairDto pairDto) {
        List<GreyList> greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(pairDto.getImei(), null, null);
        if (CollectionUtils.isNotEmpty(greyLists)) {
            return greyLists;
        }
        if (pairDto.getImsi() != null) {
            greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(greyLists)) {
                return greyLists;
            }
        }
        greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(null, null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(greyLists)) {
            return greyLists;
        }

        if (pairDto.getImsi() != null) {
            greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(greyLists)) {
                return greyLists;
            }
        }

        greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(pairDto.getImei(), null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(greyLists)) {
            return greyLists;
        }

        if (pairDto.getImsi() != null) {
            greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(greyLists)) {
                return greyLists;
            }
        }

        if (pairDto.getImsi() != null) {
            greyLists = greyListRepository.findByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(greyLists)) {
                return greyLists;
            }
        }

        return null;
    }
}
