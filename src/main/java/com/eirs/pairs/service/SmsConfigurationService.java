package com.eirs.pairs.service;

import com.eirs.pairs.constants.NotificationLanguage;
import com.eirs.pairs.constants.SmsPlaceHolders;
import com.eirs.pairs.constants.SmsTag;
import com.eirs.pairs.dto.SmsDto;

import java.util.Map;

public interface SmsConfigurationService {

    SmsDto getSms(SmsTag tag, NotificationLanguage language, String moduleName);

    SmsDto getSms(SmsTag tag, Map<SmsPlaceHolders, String> smsPlaceHolder, NotificationLanguage language, String moduleName);

}
