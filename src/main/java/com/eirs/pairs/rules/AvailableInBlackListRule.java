package com.eirs.pairs.rules;

import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.ModuleNames;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.service.BlackListService;
import com.eirs.pairs.service.SmsConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("AvailableInBlackListRule")
public class AvailableInBlackListRule implements RulesValidator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    private BlackListService blackListService;
    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Override
    public Boolean validate(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        List<Blacklist> blacklists = getByImeiAndImsiAndMsisdn(pairDto);
        log.info("Blacklist for IMEI:{} blacklists:{}", pairDto.getImei(), blacklists);
        if (CollectionUtils.isNotEmpty(blacklists)) {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_BLACKLIST_IMEI_FAIL, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_BLACKLIST_IMEI_FAIL);
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
            log.info("Black List Imei found for pairDto:{}", pairDto);
            return true;
        } else {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
            return false;
        }
    }

    private List<Blacklist> getByImeiAndImsiAndMsisdn(PairDto pairDto) {
        List<Blacklist> blacklists = blackListService.findByImeiAndImsiAndMsisdn(pairDto.getImei(), null, null);
        if (CollectionUtils.isNotEmpty(blacklists)) {
            return blacklists;
        }
        if (pairDto.getImsi() != null) {
            blacklists = blackListService.findByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(blacklists)) {
                return blacklists;
            }
        }
        blacklists = blackListService.findByImeiAndImsiAndMsisdn(null, null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(blacklists)) {
            return blacklists;
        }

        if (pairDto.getImsi() != null) {
            blacklists = blackListService.findByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), null);
            if (CollectionUtils.isNotEmpty(blacklists)) {
                return blacklists;
            }
        }

        blacklists = blackListService.findByImeiAndImsiAndMsisdn(pairDto.getImei(), null, pairDto.getMsisdn());
        if (CollectionUtils.isNotEmpty(blacklists)) {
            return blacklists;
        }

        if (pairDto.getImsi() != null) {
            blacklists = blackListService.findByImeiAndImsiAndMsisdn(null, pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(blacklists)) {
                return blacklists;
            }
        }

        if (pairDto.getImsi() != null) {
            blacklists = blackListService.findByImeiAndImsiAndMsisdn(pairDto.getImei(), pairDto.getImsi(), pairDto.getMsisdn());
            if (CollectionUtils.isNotEmpty(blacklists)) {
                return blacklists;
            }
        }

        return null;
    }
}
