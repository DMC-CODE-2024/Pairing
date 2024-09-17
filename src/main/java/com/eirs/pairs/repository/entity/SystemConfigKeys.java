package com.eirs.pairs.repository.entity;

public interface SystemConfigKeys {

    String valid_pairing_device_type = "valid_pairing_device_type";

    String default_language = "default_lang";
    String notification_sms_start_time = "notification_sms_start_time";

    String notification_sms_end_time = "notification_sms_end_time";

    String GRACE_PERIOD_END_DATE = "GRACE_PERIOD_END_DATE";

    String pairing_allowed_days = "pairing_allowed_days";

    String pairing_allowed_count = "pairing_allowed_count";

    String msisdn_min_length = "msisdn_min_length";

    String msisdn_max_length = "msisdn_max_length";

    String pairing_otp_max_valid_retries = "pairing_otp_max_valid_retries";

}
