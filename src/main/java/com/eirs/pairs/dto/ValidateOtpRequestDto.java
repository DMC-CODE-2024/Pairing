package com.eirs.pairs.dto;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.repository.entity.ImeiManualPairMgmt;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ValidateOtpRequestDto {

    private List<PairDto> pairs;

    private String serialNumber;

    private String contactNumber;

    private String emailId;

    private NotificationLanguage language;

    private Boolean isOverallAllSuccess = Boolean.TRUE;

    private String requestId;

    private Integer otp;

    public ValidateOtpRequestDto(ImeiManualPairMgmt imeiManualPairMgmt) {
        this.serialNumber = imeiManualPairMgmt.getSerialNumber();
        this.contactNumber = imeiManualPairMgmt.getContactNumber();
        this.emailId = imeiManualPairMgmt.getEmailId();
        this.language = imeiManualPairMgmt.getLanguage();
        this.requestId = imeiManualPairMgmt.getRequestId();
        this.otp = imeiManualPairMgmt.getOtp();
        this.pairs = new ArrayList<>();
        if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei1())) {
            PairDto pairDto = new PairDto();
            pairDto.setActualImei(imeiManualPairMgmt.getImei1());
            pairDto.setImei(imeiManualPairMgmt.getImei1().substring(0, 14));
            pairDto.setMsisdn(imeiManualPairMgmt.getMsisdn1());
            pairDto.setGuiMsisdn(imeiManualPairMgmt.getGuiMsisdn1());
            pairs.add(pairDto);
        }
        if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei2())) {
            PairDto pairDto = new PairDto();
            pairDto.setActualImei(imeiManualPairMgmt.getImei2());
            if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei2()))
                pairDto.setImei(imeiManualPairMgmt.getImei2().substring(0, 14));
            pairDto.setMsisdn(imeiManualPairMgmt.getMsisdn2());
            pairDto.setGuiMsisdn(imeiManualPairMgmt.getGuiMsisdn2());
            pairs.add(pairDto);
        }
        if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei3())) {
            PairDto pairDto = new PairDto();
            pairDto.setActualImei(imeiManualPairMgmt.getImei3());
            if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei3()))
                pairDto.setImei(imeiManualPairMgmt.getImei3().substring(0, 14));
            pairDto.setMsisdn(imeiManualPairMgmt.getMsisdn3());
            pairDto.setGuiMsisdn(imeiManualPairMgmt.getGuiMsisdn3());
            pairs.add(pairDto);
        }
        if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei4())) {
            PairDto pairDto = new PairDto();
            pairDto.setActualImei(imeiManualPairMgmt.getImei4());
            if (StringUtils.isNotBlank(imeiManualPairMgmt.getImei4()))
                pairDto.setImei(imeiManualPairMgmt.getImei4().substring(0, 14));
            pairDto.setMsisdn(imeiManualPairMgmt.getMsisdn4());
            pairDto.setGuiMsisdn(imeiManualPairMgmt.getGuiMsisdn4());
            pairs.add(pairDto);
        }
    }

}
