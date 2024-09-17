package com.eirs.pairs;

import com.eirs.pairs.constants.ImeiManualPairMgmtStatuses;
import com.eirs.pairs.constants.PairRequestTypes;
import com.eirs.pairs.dto.*;
import com.eirs.pairs.exception.BadRequestException;
import com.eirs.pairs.exception.ResourceNotFoundException;
import com.eirs.pairs.repository.entity.ImeiManualPairMgmt;
import com.eirs.pairs.service.ImeiManualPairMgmtService;
import com.eirs.pairs.service.SystemConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class Validator {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    ImeiManualPairMgmtService imeiManualPairMgmtService;

    public void validate(ManualPairRequestDto manualPairRequestDto) throws BadRequestException {
        if (CollectionUtils.isEmpty(manualPairRequestDto.getPairs())) {
            log.info("Validation fails for request:{}", manualPairRequestDto);
            throw new BadRequestException("Pairs must not be empty");
        }
        Set<String> imeiSet = new HashSet<>();
        for (ManualPairDto pairDto : manualPairRequestDto.getPairs()) {
            validateActualImei(pairDto.getImei());
            if (imeiSet.contains(pairDto.getImei())) {
                log.error("Validation fails Duplicate Imei:{} for request:{}", pairDto.getImei(), manualPairRequestDto);
                throw new BadRequestException("Duplicate Imei in Request");
            }
            if (StringUtils.isBlank(pairDto.getMsisdn())) {
                log.error("Validation fails as Msisdn:{} for request:{}", pairDto.getImei(), manualPairRequestDto);
                throw new BadRequestException("Msisdn can't be null or Blank");
            } else {
                pairDto.setGuiMsisdn(pairDto.getMsisdn());
                pairDto.setMsisdn(validateMsisdn(pairDto.getMsisdn()));
            }
            imeiSet.add(pairDto.getImei());
        }
        if (StringUtils.isAllBlank(manualPairRequestDto.getContactNumber(), manualPairRequestDto.getEmailId())) {
            throw new BadRequestException("Contact Number Or EmailId one must be there");
        } else {
            if (!StringUtils.isBlank(manualPairRequestDto.getContactNumber()))
                manualPairRequestDto.setContactNumber(validateMsisdn(manualPairRequestDto.getContactNumber()));
        }
    }

    public void validate(RePairRequestDto rePairRequestDto) throws BadRequestException {
        if (StringUtils.isAnyBlank(rePairRequestDto.getNewMsisdn(), rePairRequestDto.getOldMsisdn(), rePairRequestDto.getImei())) {
            log.error("Validation fails as Imei, Old and New Msisdn can't be null Blank for request:{}", rePairRequestDto.getImei(), rePairRequestDto);
            throw new BadRequestException("Imei, Old and New Msisdn can't be null Blank");
        }
        rePairRequestDto.setOldMsisdn(validateMsisdn(rePairRequestDto.getOldMsisdn()));
        rePairRequestDto.setNewMsisdn(validateMsisdn(rePairRequestDto.getNewMsisdn()));
        rePairRequestDto.setGuiNewMsisdn(validateMsisdn(rePairRequestDto.getNewMsisdn()));
        validateActualImei(rePairRequestDto.getImei());
    }

    public void validate(PairStatusRequestDto pairStatusRequestDto) throws BadRequestException {
        if (StringUtils.isAllBlank(pairStatusRequestDto.getImei(), pairStatusRequestDto.getMsisdn())) {
            throw new BadRequestException("Imei Or Msisdn one must be there");
        }
        if (StringUtils.isNotBlank(pairStatusRequestDto.getImei()))
            validateActualImei(pairStatusRequestDto.getImei());
        if (StringUtils.isNotBlank(pairStatusRequestDto.getMsisdn()))
            pairStatusRequestDto.setMsisdn(validateMsisdn(pairStatusRequestDto.getMsisdn()));

        if (StringUtils.isAllBlank(pairStatusRequestDto.getContactNumber(), pairStatusRequestDto.getEmailId())) {
            throw new BadRequestException("Contact Number Or EmailId one must be there");
        } else {
            if (!StringUtils.isBlank(pairStatusRequestDto.getContactNumber()))
                pairStatusRequestDto.setContactNumber(validateMsisdn(pairStatusRequestDto.getContactNumber()));
        }
    }

    private void validateActualImei(String actualImei) {
        if (actualImei == null || actualImei.length() < 15) {
            log.error("Validation fails as Length actualImei:{}", actualImei);
            throw new BadRequestException("Imei can't be null or length must be greater then 15");
        }
        if (!StringUtils.isNumeric(actualImei)) {
            log.error("Non Numeric Validation fails for actualImei:{}", actualImei);
            throw new BadRequestException("Imei must be Numeric");
        }
    }

    private String getMsisdn(String msisdn) {
        String retMsisdn = null;
        if (msisdn.startsWith("0")) {
            retMsisdn = "855" + msisdn.substring(1, msisdn.length());
        } else if (msisdn.startsWith("855")) {
            retMsisdn = msisdn;
        } else {
            retMsisdn = "855" + msisdn;
        }
        return retMsisdn;
    }

    private String validateMsisdn(String msisdn) {
        if (!StringUtils.isNumeric(msisdn)) {
            throw new BadRequestException("Incorrect msisdn should be numeric " + msisdn);
        }
        String finalMsisdn = getMsisdn(msisdn);

        if (finalMsisdn.length() < systemConfigurationService.getMsisdnMinLength() || finalMsisdn.length() > systemConfigurationService.getMsisdnMaxLength()) {
            throw new BadRequestException("Incorrect msisdn " + msisdn);
        }
        return finalMsisdn;
    }
}
