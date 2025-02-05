package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationChannelType;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.dto.SmsDto;
import com.eirs.pairs.exception.NotificationException;
import com.eirs.pairs.repository.OperatorSeriesRepository;
import com.eirs.pairs.repository.entity.OperatorSeries;
import com.eirs.pairs.utils.notification.NotificationUtils;
import com.eirs.pairs.utils.notification.dto.NotificationRequestDto;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationUtils notificationUtils;

    @Autowired
    SmsConfigurationService smsConfigurationService;
    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    private OperatorSeriesRepository operatorSeriesRepository;

    @Override
    public NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getMsisdn()) || notificationDetailsDto.getSmsTag() == null) {
            throw new NotificationException("Msisdn or SmsTag can't be null");
        }

        NotificationRequestDto requestDto = getSmsNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            if (requestDto.getOperatorName() == null) {
                log.info("sendSms Operator not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            requestDto.setChannelType(NotificationChannelType.SMS);
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendSms Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    @Override
    public NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getMsisdn()) || notificationDetailsDto.getSmsTag() == null) {
            throw new NotificationException("Msisdn or SmsTag can't be null");
        }
        NotificationRequestDto requestDto = getSmsNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            if (requestDto.getOperatorName() == null) {
                log.info("sendSmsInWindow Operator not found for notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            requestDto.setChannelType(NotificationChannelType.SMS);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), systemConfigurationService.getNotificationSmsStartTime());
            LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), systemConfigurationService.getNotificationSmsEndTime());
            if (now.isBefore(startDate)) {
                requestDto.setDeliveryDateTime(startDate);
            }
            if (now.isAfter(endDate)) {
                requestDto.setDeliveryDateTime(startDate.plusDays(1));
            }
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendSmsInWindow Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    @Override
    public NotificationResponseDto sendOtpSms(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getMsisdn()) || notificationDetailsDto.getSmsTag() == null) {
            throw new NotificationException("Msisdn or SmsTag can't be null");
        }
        NotificationRequestDto requestDto = getSmsNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            if (requestDto.getOperatorName() == null) {
                log.info("sendOtpSms Operator not found for response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
                return responseDto;
            }
            requestDto.setChannelType(NotificationChannelType.SMS_OTP);
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendOtpSms Notification Response notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    @Override
    public NotificationResponseDto sendEmail(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getEmailId()) || notificationDetailsDto.getSmsTag() == null || notificationDetailsDto.getSubjectSmsTag() == null) {
            throw new NotificationException("EmailId or SmsTag or Subject SmsTag can't be null");
        }
        NotificationRequestDto requestDto = getEmailNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            requestDto.setChannelType(NotificationChannelType.EMAIL);
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendOtpEmail Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    @Override
    public NotificationResponseDto sendOtpEmail(NotificationDetailsDto notificationDetailsDto) {
        if (StringUtils.isBlank(notificationDetailsDto.getEmailId()) || notificationDetailsDto.getSmsTag() == null || notificationDetailsDto.getSubjectSmsTag() == null) {
            throw new NotificationException("EmailId or SmsTag or Subject SmsTag can't be null");
        }

        NotificationRequestDto requestDto = getEmailNotificationRequestDto(notificationDetailsDto);
        NotificationResponseDto responseDto = null;
        if (requestDto != null) {
            requestDto.setChannelType(NotificationChannelType.EMAIL_OTP);
            responseDto = notificationUtils.sendNotification(requestDto);
            log.info("sendOtpEmail Notification Response:{} notificationDetailsDto:{}", responseDto, notificationDetailsDto);
        }
        return responseDto;
    }

    private NotificationRequestDto getEmailNotificationRequestDto(NotificationDetailsDto notificationDetailsDto) {
        SmsDto sms = smsConfigurationService.getSms(notificationDetailsDto.getSmsTag(), notificationDetailsDto.getSmsPlaceHolder(), notificationDetailsDto.getLanguage(), notificationDetailsDto.getModuleName());
        SmsDto subject = smsConfigurationService.getSms(notificationDetailsDto.getSubjectSmsTag(), notificationDetailsDto.getSmsPlaceHolder(), notificationDetailsDto.getLanguage(), notificationDetailsDto.getModuleName());
        log.info("Mail for subject:{} message:{} notificationDetailsDto:{} ", subject.getMsg(), sms.getMsg(), notificationDetailsDto);
        NotificationRequestDto requestDto = new NotificationRequestDto();
        requestDto.setMessage(sms.getMsg());
        requestDto.setFeatureName(notificationDetailsDto.getModuleName());
        requestDto.setSubFeature(notificationDetailsDto.getModuleName());
        requestDto.setEmail(notificationDetailsDto.getEmailId());
        requestDto.setSubject(subject.getMsg());
        requestDto.setFeatureTxnId(notificationDetailsDto.getRequestId());
        requestDto.setMsgLang(sms.getLanguage());
        requestDto.setChannelType(requestDto.getChannelType());
        return requestDto;
    }

    private NotificationRequestDto getSmsNotificationRequestDto(NotificationDetailsDto notificationDetailsDto) {
        SmsDto sms = smsConfigurationService.getSms(notificationDetailsDto.getSmsTag(), notificationDetailsDto.getSmsPlaceHolder(), notificationDetailsDto.getLanguage(), notificationDetailsDto.getModuleName());
        log.info("SMS for sms:[{}] notificationDetailsDto:{}", sms.getMsg(), notificationDetailsDto);
        NotificationRequestDto requestDto = new NotificationRequestDto();
        requestDto.setMessage(sms.getMsg());
        requestDto.setFeatureName(notificationDetailsDto.getModuleName());
        requestDto.setSubFeature(notificationDetailsDto.getModuleName());
        requestDto.setMsisdn(notificationDetailsDto.getMsisdn());
        requestDto.setMsgLang(sms.getLanguage());
        requestDto.setFeatureTxnId(notificationDetailsDto.getRequestId());
        requestDto.setOperatorName(getOperator(notificationDetailsDto.getMsisdn()));
        requestDto.setChannelType(requestDto.getChannelType());
        return requestDto;
    }

    private String getOperator(String msisdn) {
        String seriesMsisdn = msisdn.substring(0, 5);
        log.info("Going to find Operator for msisdn:{} with series:{}", msisdn, seriesMsisdn);
        Optional<OperatorSeries> operator = operatorSeriesRepository.findBySeries(Integer.parseInt(seriesMsisdn));
        if (operator.isPresent()) {
            log.info("Found Operator:{} for msisdn:{} with series:{}", operator.get(), msisdn, seriesMsisdn);
            return operator.get().getOperatorName();
        } else {
            log.info("Not Operator Found for msisdn:{} with series:{}", msisdn, seriesMsisdn);
            return "Default";
        }
    }
}
