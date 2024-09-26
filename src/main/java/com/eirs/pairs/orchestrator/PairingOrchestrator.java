
package com.eirs.pairs.orchestrator;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.*;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.exception.InternalServerException;
import com.eirs.pairs.repository.MdrRepository;
import com.eirs.pairs.repository.entity.*;
import com.eirs.pairs.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PairingOrchestrator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PairingService pairingService;

    @Autowired
    private DuplicateService duplicateService;

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private SystemConfigurationService systemConfigurationService;
    @Autowired
    private ExceptionListService exceptionListService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    InvalidImeiService invalidImeiService;
    @Autowired
    ModuleAlertService moduleAlertService;

    @Autowired
    AppConfig appConfig;

    @Transactional
    public void processForPairing(RecordDataDto recordDataDto) {
        try {
            if (!recordDataDto.getIsGsmaValid()) {
                log.info("GSMA Invalid for recordDataDto:{}", recordDataDto);
                pairingService.addPair(recordDataDto, GSMAStatus.INVALID, systemConfigurationService.getPairingAllowDays());
                invalidImeiService.save(new InvalidImei(null, recordDataDto.getActualImei(), recordDataDto.getImei()));
                sendNotification(recordDataDto, SmsTag.AutoPairGsmaInvalidSMS);
                return;
            }

            if (invalidImeiService.isPresent(recordDataDto.getImei())) {
                log.info("Invalid Imei for recordDataDto:{}", recordDataDto);
                pairingService.addPair(recordDataDto, GSMAStatus.VALID, systemConfigurationService.getPairingAllowDays());
                sendNotification(recordDataDto, SmsTag.AutoPairGsmaValidSMS);
                return;
            }

            if (!isAllowedDeviceType(recordDataDto)) {
                log.info("Not Processing record as Device Type is not Allowed from Configuration {}", recordDataDto);
                return;
            }

            if (recordDataDto.getIsCustomPaid()) {
                log.info("Not Processing as Found in Custom DB recordDataDto:{}", recordDataDto);
                return;
            }

            List<Pairing> pairings = pairingService.getPairsByImei(recordDataDto.getImei());
            Optional<Pairing> pairWithImsiOptional = pairings.stream().filter(pair ->
                    StringUtils.equals(pair.getImsi(), recordDataDto.getImsi())).findFirst();
            Optional<Pairing> pairWithNullImsiOptional = pairings.stream().filter(pair -> StringUtils.isBlank(pair.getImsi()) && StringUtils.equals(pair.getMsisdn(), recordDataDto.getMsisdn())).findFirst();
            if (pairWithImsiOptional.isPresent()) {
                log.info("Already Paired with Imei and Imsi PairMode:PAIRING FileData:{}", recordDataDto);
                if (pairWithImsiOptional.get().getRecordTime() == null) {
                    log.info("Updating EdrDateTime for pair:{}", pairWithImsiOptional.get());
                    pairWithImsiOptional.get().setRecordTime(recordDataDto.getDate());
                    pairingService.save(pairWithImsiOptional.get());
                    log.info("Updated EdrDateTime for pair:{}", pairWithImsiOptional.get());
                    exceptionListService.add(recordDataDto, PairMode.AUTO.name());
                }
            } else if (pairWithNullImsiOptional.isPresent()) {
                log.info("Already Paired with Imei and and Imsi is null PairMode:PAIRING FileData:{}", recordDataDto);
                pairWithImsiOptional.get().setRecordTime(recordDataDto.getDate());
                if (StringUtils.isBlank(pairWithImsiOptional.get().getImsi())) {
                    pairWithImsiOptional.get().setImsi(recordDataDto.getImsi());
                }
                pairingService.save(pairWithImsiOptional.get());
                log.info("Updated EdrDateTime and Imsi for pair:{}", pairWithImsiOptional.get());
                exceptionListService.add(recordDataDto, PairMode.AUTO.name());
            } else {
                if (pairings.size() < systemConfigurationService.getPairingAllowCount().intValue()) {
                    log.info("With in pair size, IMEI : {} is : {} and allowed count is : {}", recordDataDto.getImei(), pairings.size(), systemConfigurationService.getPairingAllowCount());
                    pairingService.addPair(recordDataDto, GSMAStatus.VALID, 0);
                    exceptionListService.add(recordDataDto, PairMode.AUTO.name());
                    if (duplicateService.isNotAvailable(recordDataDto.getImei())) {
                        sendNotification(recordDataDto, SmsTag.AutoPairGsmaValidSMS);
                        if ((pairings.size() + 1) == systemConfigurationService.getPairingAllowCount().intValue()) {
                            blackListService.addAndUpdate(recordDataDto);
                        }
                    }
                } else {
                    log.info("This Should not happen : {}", recordDataDto);
                    log.error("Exceeding pair count, IMEI : {} is : {} and allowed count is : {}", recordDataDto.getImei(), pairings.size(), systemConfigurationService.getPairingAllowCount());
                }
            }

        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), PairMode.AUTO.name());
        } catch (Exception e) {
            log.error("Exception while processForPairing :{}", recordDataDto, e);
        }
    }

    private boolean isAllowedDeviceType(RecordDataDto recordDataDto) {
        Set<String> allowedDeviceTypes = systemConfigurationService.getAllowedDeviceTypes();
        Optional<String> find = allowedDeviceTypes.stream().filter(deviceType -> StringUtils.equalsIgnoreCase(deviceType, recordDataDto.getDeviceType().toUpperCase())).findAny();
        return find.isPresent() ? true : false;
    }

    private void sendNotification(RecordDataDto recordDataDto, SmsTag smsTag) {
        Map<SmsPlaceHolders, String> map = new HashMap<>();
        map.put(SmsPlaceHolders.ACTUAL_IMEI, recordDataDto.getActualImei());
        map.put(SmsPlaceHolders.IMSI, recordDataDto.getImsi());
        map.put(SmsPlaceHolders.MSISDN, recordDataDto.getMsisdn());
        NotificationDetailsDto notificationDetailsDto = NotificationDetailsDto.builder().msisdn(recordDataDto.getMsisdn()).smsTag(smsTag).smsPlaceHolder(map).language(null).moduleName(appConfig.getModuleName()).build();
        try {
            notificationService.sendSmsInWindow(notificationDetailsDto);
        } catch (Exception e) {
            log.error("Notification Sms not sent notificationDetailsDto:{}", notificationDetailsDto);
        }
    }
}
