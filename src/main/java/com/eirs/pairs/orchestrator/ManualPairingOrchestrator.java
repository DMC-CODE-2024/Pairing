package com.eirs.pairs.orchestrator;

import com.eirs.pairs.Validator;
import com.eirs.pairs.alerts.AlertConfig;
import com.eirs.pairs.config.HttpsStatusConfig;
import com.eirs.pairs.constants.*;
import com.eirs.pairs.dto.*;
import com.eirs.pairs.exception.BadRequestException;
import com.eirs.pairs.exception.InternalServerException;
import com.eirs.pairs.exception.ResourceNotFoundException;
import com.eirs.pairs.mapper.PairingMapper;
import com.eirs.pairs.repository.*;
import com.eirs.pairs.repository.entity.*;
import com.eirs.pairs.rules.RulesValidator;
import com.eirs.pairs.service.*;
import com.eirs.pairs.utils.RandomIdGeneratorUtil;
import com.eirs.pairs.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ManualPairingOrchestrator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Validator validator;

    @Autowired
    private MdrRepository mdrRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HlrDumpRepository hlrDumpRepository;

    @Autowired
    private PairingService pairingService;

    @Autowired
    private ExceptionListService exceptionListService;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    SmsConfigurationService smsConfigurationService;

    @Autowired
    InvalidImeiService invalidImeiService;

    @Autowired
    ImeiPairDetailHisRepository imeiPairDetailHisRepository;

    @Autowired
    @Qualifier("AllowedDeviceTypeRule")
    RulesValidator allowedDeviceTypeRule;

    @Autowired
    @Qualifier("AvailableInCustomRule")
    RulesValidator availableInCustomRule;

    @Autowired
    @Qualifier("AvailableInBlackListRule")
    RulesValidator availableInBlackListRule;

    @Autowired
    @Qualifier("AvailableInGreyListRule")
    RulesValidator availableInGreyListRule;

    @Autowired
    @Qualifier("AvailableInExceptionListRule")
    RulesValidator availableInExceptionListRule;

    @Autowired
    @Qualifier("AllModelsAreSameRule")
    RulesValidator allModelsAreSameRule;

    @Autowired
    @Qualifier("GsmaValidRule")
    RulesValidator gsmaValidRule;

    @Autowired
    @Qualifier("AvailableInNationalWhitelistRule")
    RulesValidator availableInNationalWhitelistRule;

    @Autowired
    HttpsStatusConfig httpsStatusConfig;

    @Autowired
    ImeiManualPairMgmtService imeiManualPairMgmtService;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Autowired
    DuplicateService duplicateService;

    @Autowired
    AlertConfig alertConfig;

    public ResponseDto submitForManualPairing(ManualPairRequestDto manualPairRequestDto) {
        log.info("Manual Request received {}", manualPairRequestDto);
        validator.validate(manualPairRequestDto);
        try {
            ImeiManualPairMgmt imeiManualPairMgmt = new ImeiManualPairMgmt();
            imeiManualPairMgmt.setRequestId(RandomIdGeneratorUtil.generateRequestId());
            imeiManualPairMgmt.setOtp(RandomIdGeneratorUtil.getRandomNumbers());
            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.INIT_START.name());
            imeiManualPairMgmt.setRequestType(PairRequestTypes.PAIR);
            imeiManualPairMgmt.setOtpRetriesCount(0);
            imeiManualPairMgmt.setSerialNumber(manualPairRequestDto.getSerialNumber());
            imeiManualPairMgmt.setContactNumber(manualPairRequestDto.getContactNumber());
            imeiManualPairMgmt.setEmailId(manualPairRequestDto.getEmailId());
            imeiManualPairMgmt.setLanguage(manualPairRequestDto.getLanguage());
            imeiManualPairMgmt.setCreatedOn(LocalDateTime.now());
            imeiManualPairMgmt.setSerialNumber(manualPairRequestDto.getSerialNumber());
            for (int i = 0; i < manualPairRequestDto.getPairs().size(); i++) {
                if (i == 0) {
                    imeiManualPairMgmt.setImei1(manualPairRequestDto.getPairs().get(0).getImei());
                    imeiManualPairMgmt.setMsisdn1(manualPairRequestDto.getPairs().get(0).getMsisdn());
                    imeiManualPairMgmt.setGuiMsisdn1(manualPairRequestDto.getPairs().get(0).getGuiMsisdn());
                }
                if (i == 1) {
                    imeiManualPairMgmt.setImei2(manualPairRequestDto.getPairs().get(1).getImei());
                    imeiManualPairMgmt.setMsisdn2(manualPairRequestDto.getPairs().get(1).getMsisdn());
                    imeiManualPairMgmt.setGuiMsisdn2(manualPairRequestDto.getPairs().get(1).getGuiMsisdn());
                }
                if (i == 2) {
                    imeiManualPairMgmt.setImei3(manualPairRequestDto.getPairs().get(2).getImei());
                    imeiManualPairMgmt.setMsisdn3(manualPairRequestDto.getPairs().get(2).getMsisdn());
                    imeiManualPairMgmt.setGuiMsisdn3(manualPairRequestDto.getPairs().get(2).getGuiMsisdn());
                }
                if (i == 3) {
                    imeiManualPairMgmt.setImei4(manualPairRequestDto.getPairs().get(3).getImei());
                    imeiManualPairMgmt.setMsisdn4(manualPairRequestDto.getPairs().get(3).getMsisdn());
                    imeiManualPairMgmt.setGuiMsisdn4(manualPairRequestDto.getPairs().get(3).getGuiMsisdn());
                    break;
                }
            }

            imeiManualPairMgmt = imeiManualPairMgmtService.save(imeiManualPairMgmt);
            log.info("Saved imeiManualPairMgmt: {}", imeiManualPairMgmt);
            sentOtp(new ValidateOtpRequestDto(imeiManualPairMgmt));
            Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
            String msg = "";
            for (ManualPairDto pairDto : manualPairRequestDto.getPairs()) {
                msg = msg + pairDto.getImei() + " is paired with " + pairDto.getMsisdn() + "\n";
            }
            smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
            smsPlaceHolder.put(SmsPlaceHolders.PAIR, msg);
            return ResponseDtoUtil.getSuccessResponseDto(ManualPairResponseDto.builder().requestId(imeiManualPairMgmt.getRequestId()).response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_REQUEST_ACCEPTED)).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_REQUEST_ACCEPTED, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.info("Error while manual Error:{} pairing :{}", e.getMessage(), manualPairRequestDto, e);
            throw new InternalServerException(e.getMessage());
        }

    }

    public ResponseDto submitForPairStatus(PairStatusRequestDto pairStatusRequestDto) {
        log.info("Submit for Status request received : {}", pairStatusRequestDto);
        validator.validate(pairStatusRequestDto);
        try {
            log.info("Submit for status request received : {}", pairStatusRequestDto);
            ImeiManualPairMgmt imeiManualPairMgmt = new ImeiManualPairMgmt();
            imeiManualPairMgmt.setRequestId(RandomIdGeneratorUtil.generateRequestId());
            imeiManualPairMgmt.setOtp(RandomIdGeneratorUtil.getRandomNumbers());
            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.INIT_START.name());
            imeiManualPairMgmt.setRequestType(PairRequestTypes.PAIR_STATUS);
            imeiManualPairMgmt.setImei1(pairStatusRequestDto.getImei());
            imeiManualPairMgmt.setMsisdn1(pairStatusRequestDto.getMsisdn());
            imeiManualPairMgmt.setOtpRetriesCount(0);
            imeiManualPairMgmt.setSerialNumber(pairStatusRequestDto.getSerialNumber());
            imeiManualPairMgmt.setContactNumber(pairStatusRequestDto.getContactNumber());
            imeiManualPairMgmt.setEmailId(pairStatusRequestDto.getEmailId());
            imeiManualPairMgmt.setLanguage(pairStatusRequestDto.getLanguage());
            imeiManualPairMgmt.setCreatedOn(LocalDateTime.now());
            imeiManualPairMgmt = imeiManualPairMgmtService.save(imeiManualPairMgmt);
            log.info("Saved imeiManualPairMgmt: {}", imeiManualPairMgmt);
            sentOtp(new ValidateOtpRequestDto(imeiManualPairMgmt));
            Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
            smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
            return ResponseDtoUtil.getSuccessResponseDto(ManualPairResponseDto.builder().requestId(imeiManualPairMgmt.getRequestId()).response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_REQUEST_ACCEPTED)).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_REQUEST_ACCEPTED, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.error("Error while receiving status req  Error:{} request :{}", e.getMessage(), pairStatusRequestDto, e);
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseDto submitForRePair(RePairRequestDto rePairRequestDto) {
        log.info("Submit for re pair request received : {}", rePairRequestDto);
        validator.validate(rePairRequestDto);
        try {
            ImeiManualPairMgmt imeiManualPairMgmt = new ImeiManualPairMgmt();
            imeiManualPairMgmt.setRequestId(RandomIdGeneratorUtil.generateRequestId());
            imeiManualPairMgmt.setOtp(RandomIdGeneratorUtil.getRandomNumbers());
            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.INIT_START.name());
            imeiManualPairMgmt.setRequestType(PairRequestTypes.REPAIR);
            imeiManualPairMgmt.setImei1(rePairRequestDto.getImei());
            imeiManualPairMgmt.setMsisdn1(rePairRequestDto.getNewMsisdn());
            imeiManualPairMgmt.setGuiMsisdn1(rePairRequestDto.getGuiNewMsisdn());
            imeiManualPairMgmt.setOldMsisdn(rePairRequestDto.getOldMsisdn());
            imeiManualPairMgmt.setOtpRetriesCount(0);
            imeiManualPairMgmt.setSerialNumber(rePairRequestDto.getSerialNumber());
            Pairing pairing = pairingService.getPairsByMsisdn(rePairRequestDto.getImei().substring(0, 14), rePairRequestDto.getOldMsisdn());
            if (pairing == null) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_OLD_PAIR_NOT_FOUND_FAIL, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String status = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_OLD_PAIR_NOT_FOUND_FAIL);
                log.info("Old Pair Doesn't found {} ", rePairRequestDto);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(status).description(description.getMsg()).build());
            }
            if (pairing.getPairMode() == PairMode.MANUAL) {
                try {
                    List<ImeiManualPairMgmt> existing = imeiManualPairMgmtService.findByImeiAndMsisdn(rePairRequestDto.getImei(), rePairRequestDto.getOldMsisdn(), PairRequestTypes.PAIR);
                    Optional<ImeiManualPairMgmt> optional = existing.stream().filter(data -> (StringUtils.equalsIgnoreCase(data.getStatus(), ImeiManualPairMgmtStatuses.DONE.name()))).findAny();
                    imeiManualPairMgmt.setContactNumber(optional.get().getContactNumber());
                    imeiManualPairMgmt.setEmailId(optional.get().getEmailId());
                } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
                    log.error("Error {}", e.getMessage(), e);
                    moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
                } catch (ResourceNotFoundException resourceNotFoundException) {
                    log.error("As Old Pair with MANUAL, But didn't found in ImeiManualPairMgmt for :{}", rePairRequestDto);
                }
            } else {
                imeiManualPairMgmt.setContactNumber(pairing.getMsisdn());
            }
            imeiManualPairMgmt.setLanguage(rePairRequestDto.getLanguage());
            imeiManualPairMgmt.setCreatedOn(LocalDateTime.now());
            imeiManualPairMgmt = imeiManualPairMgmtService.save(imeiManualPairMgmt);
            log.info("Saved imeiManualPairMgmt: {}", imeiManualPairMgmt);
            sentOtp(new ValidateOtpRequestDto(imeiManualPairMgmt));
            Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
            smsPlaceHolder.put(SmsPlaceHolders.PAIR, rePairRequestDto.getImei() + " is paired with " + rePairRequestDto.getNewMsisdn());
            smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
            return ResponseDtoUtil.getSuccessResponseDto(ManualPairResponseDto.builder().requestId(imeiManualPairMgmt.getRequestId()).response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_REQUEST_ACCEPTED)).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_REQUEST_ACCEPTED, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            log.info("Error while receiving re-pair req  Error:{} request :{}", e.getMessage(), rePairRequestDto, e);
            throw new InternalServerException(e.getMessage());
        }
    }

    public ResponseDto reSendOtp(ReSendOtpRequestDto reSendOtpRequestDto) {
        try {
            log.info("Request for Resend OTP for reSendOtpRequestDto:{}", reSendOtpRequestDto);
            ImeiManualPairMgmt imeiManualPairMgmt = imeiManualPairMgmtService.findByRequestId(reSendOtpRequestDto.getRequestId());
            if (imeiManualPairMgmt == null) {
                log.info("Not Found imeiManualPairMgmt for Resend OTP for reSendOtpRequestDto:{}", reSendOtpRequestDto);
                return ResponseDtoUtil.getErrorResponseDto(ErrorInfo.builder().errorMessage("Request Id is Invalid").build());
            }
            if (ImeiManualPairMgmtStatuses.INIT_START.name().equals(imeiManualPairMgmt.getStatus())) {
                imeiManualPairMgmt.setOtp(RandomIdGeneratorUtil.getRandomNumbers());
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                sentOtp(new ValidateOtpRequestDto(imeiManualPairMgmt));
                Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
                smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_RESEND_OTP)).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_RESEND_OTP, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
            } else {
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_REQUEST_ALREADY_PROCESSED)).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_REQUEST_ALREADY_PROCESSED, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
            }
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        }
    }

    private ResponseDto successVerifyOtpStatus(ImeiManualPairMgmt imeiManualPairMgmt) {
        try {
            List<Pairing> pairings = getPairsForStatus(imeiManualPairMgmt);
            if (pairings == null) {
                String status = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_NOT_FOUND_FAIL);
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_PAIR_NOT_FOUND_FAIL, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmt.setFailReason(SmsTag.HTTP_RESP_PAIR_NOT_FOUND_FAIL.getDescription());
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(status).description(description.getMsg()).build());
            }
            List<PairingDto> pairingDtos = Mappers.getMapper(PairingMapper.class).toPairingDtos(pairings);
            // Masking
            if (StringUtils.isBlank(imeiManualPairMgmt.getMsisdn1())) {
                pairingDtos.stream().forEach(pairingDto -> pairingDto.setMsisdn(StringUtil.masking(pairingDto.getMsisdn())));
            } else if (StringUtils.isBlank(imeiManualPairMgmt.getImei1())) {
                pairingDtos.stream().forEach(pairingDto -> {
                    pairingDto.setImei(StringUtil.masking(pairingDto.getImei()));
                    pairingDto.setActualImei(StringUtil.masking(pairingDto.getActualImei()));
                });
            }
            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.DONE.name());
            imeiManualPairMgmt.setFailReason(SmsTag.HTTP_RESP_OTP_VALIDATION_SUCCESS.getDescription());
            imeiManualPairMgmtService.save(imeiManualPairMgmt);
            log.info("Otp Validation Success Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
            return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().pairs(pairingDtos).response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_OTP_VALIDATION_SUCCESS)).description("Success").build());
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        } catch (RuntimeException e) {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_INVALID_IMSI_NOT_FOUND, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String status = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_INVALID_IMSI_NOT_FOUND);
            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
            imeiManualPairMgmt.setFailReason(SmsTag.HTTP_RESP_INVALID_IMSI_NOT_FOUND.getDescription());
            imeiManualPairMgmtService.save(imeiManualPairMgmt);
            return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(status).description(description.getMsg()).build());
        }
    }

    @Transactional
    private ResponseDto doRePair(ImeiManualPairMgmt imeiManualPairMgmt) {
        try {
            ValidateOtpRequestDto validateOtpRequestDto = new ValidateOtpRequestDto(imeiManualPairMgmt);

            PairDto newPair = validateOtpRequestDto.getPairs().get(0);
            PairDto oldPair = new PairDto(newPair.getImei(), imeiManualPairMgmt.getOldMsisdn());
            // Check Old Pair Exist
            Pairing oldPairing = pairingService.getPairsByMsisdn(oldPair.getImei(), oldPair.getMsisdn());
            log.info("Repair Old Pair found for oldPair:{} imeiManualPairMgmt:{}", oldPair, imeiManualPairMgmt);
            if (oldPairing == null) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_OLD_PAIR_NOT_FOUND_FAIL, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String status = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_OLD_PAIR_NOT_FOUND_FAIL);
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmt.setFailReason(description.getMsg());
                imeiManualPairMgmt.setStatus1(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmt.setDescription1(description.getMsg());
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                log.info("Old Pair Doesn't found | Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                sendNotificationFailedRepair(imeiManualPairMgmt);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(status).description(description.getMsg()).pairsStatus(validateOtpRequestDto.getPairs()).build());
            }

            // Check Already Paired
            List<HlrDumpEntity> oldHlrDump = getImsi(oldPair.getMsisdn());
            if (CollectionUtils.isNotEmpty(oldHlrDump) && oldHlrDump.size() == 1) {
                oldPair.setImsi(oldHlrDump.get(0).getImsi());
                oldPair.setOperator(oldHlrDump.get(0).getOperatorName());
            }
            List<HlrDumpEntity> hlrDump = getImsi(newPair.getMsisdn());
            if (CollectionUtils.isNotEmpty(hlrDump) && hlrDump.size() == 1) {
                newPair.setImsi(hlrDump.get(0).getImsi());
                newPair.setOperator(hlrDump.get(0).getOperatorName());
            }

            if (StringUtils.isBlank(oldPair.getImsi()) || StringUtils.isBlank(newPair.getImsi())) {
                log.info("Not Updating Duplicate as Imsi is blank OldImsi:{} NewImsi:{}", oldPair.getImsi(), newPair.getImsi());
            } else {
                Duplicate duplicate = duplicateService.get(oldPair.getImei(), oldPair.getImsi());
                if (duplicate != null) {
                    log.info("Updating Duplicate with duplicate:{} oldPair:{} newPair:{}", duplicate, oldPair, newPair);
                    duplicate.setMsisdn(newPair.getMsisdn());
                    duplicate.setImsi(newPair.getImsi());
                    duplicateService.save(duplicate);
                }
            }

            List<ExceptionList> exceptionLists = null;
            if (StringUtils.isNotBlank(oldPair.getImsi())) {
                exceptionLists = exceptionListService.getByImeiAndImsi(oldPair.getImei(), oldPair.getImsi());
            } else {
                exceptionLists = exceptionListService.getByImeiAndImsiAndMsisdn(oldPair.getImei(), oldPair.getImsi(), oldPair.getMsisdn());
            }
            if (CollectionUtils.isNotEmpty(exceptionLists)) {
                exceptionListService.delete(oldPair, PairMode.REPAIR.name(), exceptionLists);
                exceptionListService.add(newPair, imeiManualPairMgmt.getRequestId(), PairMode.REPAIR.name());
            }

            log.info("Got Already Pair :{} Updating with new Pair:{}", oldPairing, newPair);
            ImeiPairDetailHis imeiPairDetailHis = Mappers.getMapper(PairingMapper.class).toImeiPairDetailHis(oldPairing);
            imeiPairDetailHis.setAction("UPDATE");
            imeiPairDetailHis.setActionRemark(PairMode.REPAIR.name());
            log.info("Going to save in Pairing history : {}", imeiPairDetailHis);
            ImeiPairDetailHis savedHistoryPair = imeiPairDetailHisRepository.save(imeiPairDetailHis);
            log.info("Saved pairing history : {}", savedHistoryPair);
            oldPairing.setMsisdn(newPair.getMsisdn());
            oldPairing.setImsi(newPair.getImsi());
            oldPairing.setRequestId(validateOtpRequestDto.getRequestId());
            pairingService.save(oldPairing);

            imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.DONE.name());
            imeiManualPairMgmt.setFailReason("");
            imeiManualPairMgmt.setStatus1(ImeiManualPairMgmtStatuses.DONE.name());
            imeiManualPairMgmt.setDescription1("");

            imeiManualPairMgmtService.save(imeiManualPairMgmt);
            sendNotificationRepair(imeiManualPairMgmt);
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_REPAIR_SUCCESS, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            log.info("Otp Validation Success Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
            return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_REPAIR_SUCCESS)).description(description.getMsg()).pairsStatus(validateOtpRequestDto.getPairs()).build());
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        }
    }


    @Transactional
    private ResponseDto doPair(ImeiManualPairMgmt imeiManualPairMgmt) {
        try {
            ValidateOtpRequestDto validateOtpRequestDto = new ValidateOtpRequestDto(imeiManualPairMgmt);
            for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
                String tac = pairDto.getImei().substring(0, 8);
                MdrEntity mdrEntity = getModelByTac(tac);
                if (mdrEntity != null) {
                    pairDto.setModel(mdrEntity.getModel());
                    pairDto.setDeviceType(mdrEntity.getDeviceType());
                }
                List<HlrDumpEntity> hlrDump = getImsi(pairDto.getMsisdn());
                if (CollectionUtils.isNotEmpty(hlrDump) && hlrDump.size() == 1) {
                    pairDto.setImsi(hlrDump.get(0).getImsi());
                    pairDto.setOperator(hlrDump.get(0).getOperatorName());
                }
            }
            if (!gsmaValidRule.validate(null, validateOtpRequestDto)) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_FAIL_GSMA_INVALID, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_FAIL_GSMA_INVALID);
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmt.setFailReason(description.getMsg());
                mapStatus(validateOtpRequestDto, imeiManualPairMgmt);
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                log.info("GSMA Invalid Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                sendNotificationFailNotification(validateOtpRequestDto);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpResp).description(description.getMsg()).pairsStatus(validateOtpRequestDto.getPairs()).build());

            }
            if (!allModelsAreSameRule.validate(null, validateOtpRequestDto)) {
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_FAIL_DEVICE_MODELS_ARE_NOT_SAME, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_FAIL_DEVICE_MODELS_ARE_NOT_SAME);
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmt.setFailReason(description.getMsg());
                mapStatus(validateOtpRequestDto, imeiManualPairMgmt);
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                log.info("Models are not same Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                sendNotificationFailNotification(validateOtpRequestDto);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpResp).description(description.getMsg()).pairsStatus(validateOtpRequestDto.getPairs()).build());
            }

            for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
                if (!allowedDeviceTypeRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (invalidImeiService.isPresent(pairDto.getImei())) {
                    SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_INVALID_IMEI_FAIL, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                    String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_INVALID_IMEI_FAIL);
                    pairDto.setStatus(httpResp);
                    pairDto.setDescription(description.getMsg());
                    continue;
                }
                if (availableInCustomRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (checkAlreadyPaired(pairDto, validateOtpRequestDto)) continue;
//            if (availableInDuplicateRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (availableInBlackListRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (availableInGreyListRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (checkPairCountLimit(pairDto, validateOtpRequestDto)) continue;
                if (availableInExceptionListRule.validate(pairDto, validateOtpRequestDto)) continue;
                if (availableInNationalWhitelistRule.validate(pairDto, validateOtpRequestDto)) ;

            }
            mapStatus(validateOtpRequestDto, imeiManualPairMgmt);
            String failHttpResponse = "";
            for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
                if (!StringUtils.equals(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS), pairDto.getStatus())) {
                    validateOtpRequestDto.setIsOverallAllSuccess(Boolean.FALSE);
                    failHttpResponse = pairDto.getStatus();
                    break;
                }
            }
            if (validateOtpRequestDto.getIsOverallAllSuccess()) {
                addPair(validateOtpRequestDto, PairMode.MANUAL, GSMAStatus.VALID, 0);
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.DONE.name());
                imeiManualPairMgmt.setFailReason("");
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                sendNotificationPair(validateOtpRequestDto);
                SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_PAIR_SUCCESS, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS)).description(description.getMsg()).pairsStatus(validateOtpRequestDto.getPairs()).build());
            } else {
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                sendNotificationFailNotification(validateOtpRequestDto);
                return ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(failHttpResponse).pairsStatus(validateOtpRequestDto.getPairs()).build());
            }
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.MANUAL.name());
            throw new InternalServerException(e.getMessage());
        }
    }

    private void mapStatus(ValidateOtpRequestDto validateOtpRequestDto, ImeiManualPairMgmt imeiManualPairMgmt) {
        if (imeiManualPairMgmt.getImei1() != null) {
            PairDto pairDto = validateOtpRequestDto.getPairs().get(0);
            imeiManualPairMgmt.setStatus1(pairDto.getStatus());
            imeiManualPairMgmt.setDescription1(pairDto.getDescription());
        }
        if (imeiManualPairMgmt.getImei2() != null) {
            PairDto pairDto = validateOtpRequestDto.getPairs().get(1);
            imeiManualPairMgmt.setStatus2(pairDto.getStatus());
            imeiManualPairMgmt.setDescription2(pairDto.getDescription());
        }
        if (imeiManualPairMgmt.getImei3() != null) {
            PairDto pairDto = validateOtpRequestDto.getPairs().get(2);
            imeiManualPairMgmt.setStatus3(pairDto.getStatus());
            imeiManualPairMgmt.setDescription3(pairDto.getDescription());
        }
        if (imeiManualPairMgmt.getImei4() != null) {
            PairDto pairDto = validateOtpRequestDto.getPairs().get(3);
            imeiManualPairMgmt.setStatus4(pairDto.getStatus());
            imeiManualPairMgmt.setDescription4(pairDto.getDescription());
        }
    }

    private Boolean checkAlreadyPaired(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        Boolean alreadyPaired = false;
        String alreadyPairDesc = smsConfigurationService.getSms(SmsTag.HTTP_RESP_ALREADY_PAIRED, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg();
        String alreadyPairHttpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_ALREADY_PAIRED);
        Pairing pairing = pairingService.getPairsByMsisdn(pairDto.getImei(), pairDto.getMsisdn());
        if (pairing == null) {
            pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_SUCCESS));
        } else {
            pairDto.setStatus(alreadyPairHttpResp);
            pairDto.setDescription(alreadyPairDesc);
            alreadyPaired = true;
        }
        return alreadyPaired;
    }

    private Boolean checkPairCountLimit(PairDto pairDto, ValidateOtpRequestDto validateOtpRequestDto) {
        int allowedPairCount = systemConfigurationService.getPairingAllowCount().intValue();
        List<Pairing> pairings = pairingService.getPairsByImei(pairDto.getImei());
        log.info("Pairing for IMEI:{} pairings:{} validateOtpRequestDto:{}", pairDto.getImei(), pairings, validateOtpRequestDto);
        int afterSize = pairings.size() + 1;
        log.info("Checking Pairing afterSize:{} AllowedCount:{} validateOtpRequestDto:{}", afterSize, allowedPairCount, validateOtpRequestDto);
        if (afterSize > allowedPairCount) {
            SmsDto description = smsConfigurationService.getSms(SmsTag.HTTP_RESP_PAIR_COUNT_LIMIT_FAIL, validateOtpRequestDto.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME);
            String httpResp = httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_PAIR_COUNT_LIMIT_FAIL);
            pairDto.setStatus(httpResp);
            pairDto.setDescription(description.getMsg());
            return true;
        }
        pairDto.setStatus(httpsStatusConfig.getStatus(SmsTag.HTTP_RESP_OTP_VALIDATION_SUCCESS));
        return false;
    }

    private List<HlrDumpEntity> getImsi(String msisdn) {
        log.info("Find in hlr table msisdn : {}", msisdn);
        List<HlrDumpEntity> hlrDump = hlrDumpRepository.findByMsisdn(msisdn);
        log.info("HLR Dump details from msisdn:{} HlrDumpEntity:{}", msisdn, hlrDump);
        return hlrDump;
    }

    private void sentOtp(ValidateOtpRequestDto validateOtpRequestDto) {
        Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
        String msg = "";
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            msg = msg + pairDto.getActualImei() + " is paired with " + pairDto.getMsisdn() + "\n";
        }
        smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, validateOtpRequestDto.getRequestId());
        smsPlaceHolder.put(SmsPlaceHolders.PAIR, msg);
        smsPlaceHolder.put(SmsPlaceHolders.OTP, String.valueOf(validateOtpRequestDto.getOtp()));
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(validateOtpRequestDto.getContactNumber()).smsTag(SmsTag.ManualPairOtpSMS).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(validateOtpRequestDto.getRequestId()).build();
            notificationService.sendOtpSms(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Sms not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().emailId(validateOtpRequestDto.getEmailId()).smsTag(SmsTag.ManualPairOtpEmail).subjectSmsTag(SmsTag.ManualPairOtpSubject).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(validateOtpRequestDto.getRequestId()).build();
            notificationService.sendOtpEmail(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Email not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
    }

    private void sendNotificationPair(ValidateOtpRequestDto validateOtpRequestDto) {
        Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
        String msg = "";
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            msg = msg + pairDto.getActualImei() + " is paired with " + pairDto.getMsisdn() + "\n";
        }
        smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, validateOtpRequestDto.getRequestId());
        smsPlaceHolder.put(SmsPlaceHolders.PAIR, msg);
        try {
            notificationService.sendSms(NotificationDetailsDto.builder().msisdn(validateOtpRequestDto.getContactNumber()).smsTag(SmsTag.ManualPairSMS).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).requestId(validateOtpRequestDto.getRequestId()).moduleName(alertConfig.getProcessId()).build());
        } catch (Exception e) {
            log.error("Notification Sms not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().emailId(validateOtpRequestDto.getEmailId()).smsTag(SmsTag.ManualPairEmail).subjectSmsTag(SmsTag.ManualPairSubject).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(validateOtpRequestDto.getRequestId()).build();
            notificationService.sendEmail(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Email not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
    }

    private void sendNotificationFailNotification(ValidateOtpRequestDto validateOtpRequestDto) {
        Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
        String msg = "";
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            msg = msg + pairDto.getActualImei() + " with " + pairDto.getMsisdn() + ", status:" + pairDto.getStatus() + "\n";
        }
        smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, validateOtpRequestDto.getRequestId());
        smsPlaceHolder.put(SmsPlaceHolders.PAIR, msg);
        try {
            notificationService.sendSms(NotificationDetailsDto.builder().msisdn(validateOtpRequestDto.getContactNumber()).smsTag(SmsTag.ManualPairFailedSMS).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).requestId(validateOtpRequestDto.getRequestId()).moduleName(alertConfig.getProcessId()).build());
        } catch (Exception e) {
            log.error("Notification Sms not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().emailId(validateOtpRequestDto.getEmailId()).smsTag(SmsTag.ManualPairFailedEmail).subjectSmsTag(SmsTag.ManualPairSubject).smsPlaceHolder(smsPlaceHolder).language(validateOtpRequestDto.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(validateOtpRequestDto.getRequestId()).build();
            notificationService.sendEmail(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Email not sent validateOtpRequestDto:{}", validateOtpRequestDto);
        }
    }

    private void sendNotificationRepair(ImeiManualPairMgmt imeiManualPairMgmt) {
        log.info("Sending Notification smsTag:{} on Sms :{}", SmsTag.ManualPairRepairSMS, imeiManualPairMgmt);
        Map<SmsPlaceHolders, String> placeHolder = new HashMap<>();
        placeHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
        placeHolder.put(SmsPlaceHolders.ACTUAL_IMEI, imeiManualPairMgmt.getImei1());
        placeHolder.put(SmsPlaceHolders.OLD_MSISDN, imeiManualPairMgmt.getOldMsisdn());
        placeHolder.put(SmsPlaceHolders.MSISDN, imeiManualPairMgmt.getMsisdn1());
        placeHolder.put(SmsPlaceHolders.OTP, String.valueOf(imeiManualPairMgmt.getOtp()));
        String msg = imeiManualPairMgmt.getImei1() + " is paired with " + imeiManualPairMgmt.getMsisdn1();
        placeHolder.put(SmsPlaceHolders.PAIR, msg);
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(imeiManualPairMgmt.getContactNumber()).smsTag(SmsTag.ManualPairRepairSMS).smsPlaceHolder(placeHolder).language(imeiManualPairMgmt.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(imeiManualPairMgmt.getRequestId()).build();
            notificationService.sendSms(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Sms not sent imeiManualPairMgmt:{}", imeiManualPairMgmt);
        }
        log.info("Sending Notification smsTag:{} on Email :{}", SmsTag.ManualPairRepairEmail, imeiManualPairMgmt);
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().emailId(imeiManualPairMgmt.getEmailId()).smsTag(SmsTag.ManualPairRepairEmail).subjectSmsTag(SmsTag.ManualPairRepairSubject).smsPlaceHolder(placeHolder).language(imeiManualPairMgmt.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(imeiManualPairMgmt.getRequestId()).build();
            notificationService.sendEmail(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Email not sent imeiManualPairMgmt:{}", imeiManualPairMgmt);
        }
    }

    private void sendNotificationFailedRepair(ImeiManualPairMgmt imeiManualPairMgmt) {
        log.info("Sending Notification smsTag:{} on Sms :{}", SmsTag.ManualPairRepairSMS, imeiManualPairMgmt);
        Map<SmsPlaceHolders, String> placeHolder = new HashMap<>();
        placeHolder.put(SmsPlaceHolders.REFERENCE_ID, imeiManualPairMgmt.getRequestId());
        placeHolder.put(SmsPlaceHolders.ACTUAL_IMEI, imeiManualPairMgmt.getImei1());
        placeHolder.put(SmsPlaceHolders.OLD_MSISDN, imeiManualPairMgmt.getOldMsisdn());
        placeHolder.put(SmsPlaceHolders.MSISDN, imeiManualPairMgmt.getMsisdn1());
        placeHolder.put(SmsPlaceHolders.OTP, String.valueOf(imeiManualPairMgmt.getOtp()));
        String msg = imeiManualPairMgmt.getImei1() + " is not paired with " + imeiManualPairMgmt.getMsisdn1();
        placeHolder.put(SmsPlaceHolders.PAIR, msg);
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(imeiManualPairMgmt.getContactNumber()).smsTag(SmsTag.ManualPairRepairFailedSMS).smsPlaceHolder(placeHolder).language(imeiManualPairMgmt.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(imeiManualPairMgmt.getRequestId()).build();
            notificationService.sendSms(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Sms not sent imeiManualPairMgmt:{}", imeiManualPairMgmt);
        }
        log.info("Sending Notification smsTag:{} on Email :{}", SmsTag.ManualPairRepairEmail, imeiManualPairMgmt);
        try {
            NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().emailId(imeiManualPairMgmt.getEmailId()).smsTag(SmsTag.ManualPairRepairFailedEmail).subjectSmsTag(SmsTag.ManualPairRepairSubject).smsPlaceHolder(placeHolder).language(imeiManualPairMgmt.getLanguage()).moduleName(alertConfig.getProcessId()).requestId(imeiManualPairMgmt.getRequestId()).build();
            notificationService.sendEmail(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Email not sent imeiManualPairMgmt:{}", imeiManualPairMgmt);
        }
    }

    private MdrEntity getModelByTac(String tac) {
        log.info("Find in MDR table tac : {}", tac);
        MdrEntity mdrEntity = mdrRepository.findByTac(tac);
        log.info("Model found from tac:{} Mdr:{}", tac, mdrEntity);
        if (mdrEntity == null) {
            return null;
        }
        return mdrEntity;
    }


    private List<Pairing> getPairsForStatus(ImeiManualPairMgmt imeiManualPairMgmt) {
        if (StringUtils.isBlank(imeiManualPairMgmt.getMsisdn1())) {
            List<Pairing> pairings = pairingService.getPairsByActualImei(imeiManualPairMgmt.getImei1());
            if (CollectionUtils.isEmpty(pairings)) return null;
            return pairings;
        } else if (StringUtils.isBlank(imeiManualPairMgmt.getImei1())) {
            List<Pairing> pairings = pairingService.getPairsByMsisdn(imeiManualPairMgmt.getMsisdn1());
            if (CollectionUtils.isEmpty(pairings)) return null;
            return pairings;
        } else {
            Pairing pairing = pairingService.getPairsActualImeiByMsisdn(imeiManualPairMgmt.getImei1(), imeiManualPairMgmt.getMsisdn1());
            log.info("Pairing found for Msisdn1 imeiManualPairMgmt:{}", imeiManualPairMgmt);
            if (pairing == null) {
                return null;
            } else {
                List<Pairing> pairings = new ArrayList<>();
                pairings.add(pairing);
                return pairings;
            }
        }
    }

    private void addPair(ValidateOtpRequestDto validateOtpRequestDto, PairMode pairMode, GSMAStatus gsmaStatus, int allowedDays) {
        List<Pairing> list = new ArrayList<>();
        for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
            Pairing paring = Pairing.builder().requestId(validateOtpRequestDto.getRequestId()).pairingDate(LocalDateTime.now()).txnId(validateOtpRequestDto.getRequestId()).operator(pairDto.getOperator()).msisdn(pairDto.getMsisdn()).pairMode(pairMode).gsmaStatus(gsmaStatus).filename("").allowedDays(allowedDays).imei(pairDto.getImei()).actualImei(pairDto.getActualImei()).imsi(pairDto.getImsi()).pairMode(PairMode.MANUAL).build();
            if (allowedDays > 0) paring.setExpiryDate(LocalDateTime.now().plusDays(allowedDays));
            list.add(paring);
            log.info("Added to save paring:{} , validateOtpRequestDto:{}", paring, validateOtpRequestDto);
        }
        pairingService.saveAll(list);
    }

    public ResponseDto verifyOtp(OtpVerifyRequestDto requestDto) {
        ResponseDto responseDto = null;
        try {
            Map<SmsPlaceHolders, String> smsPlaceHolder = new HashMap<>();
            smsPlaceHolder.put(SmsPlaceHolders.REFERENCE_ID, requestDto.getRequestId());
            smsPlaceHolder.put(SmsPlaceHolders.OTP, String.valueOf(requestDto.getOtp()));
            ImeiManualPairMgmt imeiManualPairMgmt = imeiManualPairMgmtService.findByRequestId(requestDto.getRequestId());

            if (ImeiManualPairMgmtStatuses.FAIL.name().equals(imeiManualPairMgmt.getStatus()) || ImeiManualPairMgmtStatuses.DONE.name().equals(imeiManualPairMgmt.getStatus())) {
                responseDto = ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(SmsTag.HTTP_RESP_REQUEST_ALREADY_PROCESSED.getHttpResp()).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_REQUEST_ALREADY_PROCESSED, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
            }

            imeiManualPairMgmt.setOtpRetriesCount(imeiManualPairMgmt.getOtpRetriesCount() + 1);
            log.info("Otp Verify Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
            if (imeiManualPairMgmt == null) {
                log.info("OTP Verification imeiManualPairMgmt not found for requestDto: {}", requestDto);
                throw new ResourceNotFoundException("Invalid Request Id " + requestDto.getRequestId());
            }
            if (requestDto.getOtp().intValue() == imeiManualPairMgmt.getOtp().intValue()) {
                imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.INIT.name());
                imeiManualPairMgmtService.save(imeiManualPairMgmt);
                log.info("OTP is valid for requestDto: {}", requestDto);
                switch (imeiManualPairMgmt.getRequestType()) {
                    case PAIR -> {
                        responseDto = doPair(imeiManualPairMgmt);
                    }
                    case REPAIR -> {
                        responseDto = doRePair(imeiManualPairMgmt);
                    }
                    case PAIR_STATUS -> {
                        responseDto = successVerifyOtpStatus(imeiManualPairMgmt);
                    }
                }
            } else {
                if (systemConfigurationService.getMaxOtpValidRetries() >= imeiManualPairMgmt.getOtpRetriesCount()) {
                    log.info("OTP is Invalid for requestDto: {}", requestDto);
                    imeiManualPairMgmt.setFailReason(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL.getDescription());
                    imeiManualPairMgmtService.save(imeiManualPairMgmt);
                    Integer leftAttempts = systemConfigurationService.getMaxOtpValidRetries() - imeiManualPairMgmt.getOtpRetriesCount();
                    log.info("Otp Invalid Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                    smsPlaceHolder.put(SmsPlaceHolders.OTP_COUNT_LEFT, String.valueOf(leftAttempts));
                    responseDto = ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL.getHttpResp()).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
                } else {
                    log.info("Otp Limit Exhausted Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                    imeiManualPairMgmt.setStatus(ImeiManualPairMgmtStatuses.FAIL.name());
                    imeiManualPairMgmt.setFailReason(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL_MAX_RETRY.getDescription());
                    imeiManualPairMgmtService.save(imeiManualPairMgmt);
                    log.info("Otp Invalid Saved imeiManualPairMgmt:{} ", imeiManualPairMgmt);
                    responseDto = ResponseDtoUtil.getSuccessResponseDto(OtpVerifyResponseDto.builder().response(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL_MAX_RETRY.getHttpResp()).description(smsConfigurationService.getSms(SmsTag.HTTP_RESP_OTP_VALIDATION_FAIL_MAX_RETRY, smsPlaceHolder, imeiManualPairMgmt.getLanguage(), ModuleNames.MANUAL_PAIRING_MODULE_NAME).getMsg()).build());
                }
            }
        } catch (Exception e) {
            log.info("Error while Verify OTP Error:{} requestId :{}", e.getMessage(), requestDto.getRequestId(), e);
            throw new InternalServerException(e.getMessage());
        }
        return responseDto;
    }
}
