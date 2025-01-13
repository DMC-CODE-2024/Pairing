package com.eirs.pairs.repository.entity;

public interface SystemConfigKeys {

    String valid_pairing_device_type = "pairing_allowed_device_type";

    String default_language = "systemDefaultLanguage";
    String notification_sms_start_time = "pairing_notification_sms_start_time";

    String notification_sms_end_time = "pairing_notification_sms_end_time";

    String GRACE_PERIOD_END_DATE = "GRACE_PERIOD_END_DATE";

    String pairing_allowed_days = "pairing_allowed_days";

    String pairing_allowed_count = "pairing_allowed_count";

    String send_pairing_notification_flag = "pairing_send_notification_flag";
}
