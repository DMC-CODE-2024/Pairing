package com.eirs.pairs.service;

import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.exception.NotificationException;
import com.eirs.pairs.utils.notification.dto.NotificationResponseDto;

public interface NotificationService {
    NotificationResponseDto sendSms(NotificationDetailsDto notificationDetailsDto) throws NotificationException;

    NotificationResponseDto sendSmsInWindow(NotificationDetailsDto notificationDetailsDto) throws NotificationException;

    NotificationResponseDto sendOtpSms(NotificationDetailsDto notificationDetailsDto) throws NotificationException;

    NotificationResponseDto sendEmail(NotificationDetailsDto notificationDetailsDto) throws NotificationException;

    NotificationResponseDto sendOtpEmail(NotificationDetailsDto notificationDetailsDto) throws NotificationException;
}
