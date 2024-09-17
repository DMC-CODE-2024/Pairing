package com.eirs.pairs.constants;

public enum SmsTag {
    AutoPairGsmaValidSMS("", "Your <MSISDN> has been auto paired with <ACTUAL_IMEI>."),
    AutoPairGsmaInvalidSMS("", "Your <MSISDN> has been auto paired with <ACTUAL_IMEI>."),
    ManualPairRepairSMS("", "Your repair request with reference number <REFERENCE_ID> is repaired successfully. The new pair detail is <PAIR>"),
    ManualPairRepairEmail("", "Your repair request with reference number <REFERENCE_ID> is repaired successfully. The new pair detail is <PAIR>"),

    ManualPairRepairFailedSMS("", "Your repair request failed with reference number <REFERENCE_ID> for <PAIR>"),
    ManualPairRepairFailedEmail("", "Your repair request failed with reference number <REFERENCE_ID> for <PAIR>"),

    ManualPairRepairSubject("", " Repaired Notification #<REFERENCE_ID>"),
    ManualPairEmail("", "Your pairing request with reference number <REFERENCE_ID> is paired successfully. The pairing details are <PAIR>"),
    ManualPairSMS("", "Your pairing request with reference number <REFERENCE_ID> is paired successfully. The pairing details are <PAIR>"),

    ManualPairFailedEmail("", "Your pairing request with reference number <REFERENCE_ID> is not paired. The pairing details are <PAIR>"),
    ManualPairFailedSMS("", "Your pairing request with reference number <REFERENCE_ID> is not paired. The pairing details are <PAIR>"),
    ManualPairOtpSMS("", "Your request is accepted. This is OTP <OTP> for your reference number <REFERENCE_ID>"),
    ManualPairOtpEmail("", "Your request is accepted. This is OTP <OTP> for your reference number <REFERENCE_ID>"),
    ManualPairSubject("", "Paired Notification #<REFERENCE_ID>"),
    ManualPairOtpSubject("", "OTP for Reference Number <REFERENCE_ID>"),
    HTTP_RESP_REQUEST_ACCEPTED("Success", "Your request has been accepted. The OTP has been sent to your mobile/emailId for reference number <REFERENCE_ID>"),

    HTTP_RESP_RESEND_OTP("Success", "OTP has been resent for reference number <REFERENCE_ID> to your Mobile/EmailId"),
    HTTP_RESP_OTP_VALIDATION_SUCCESS("Success", "Otp Valid"),

    HTTP_RESP_PAIR_SUCCESS("Success", "Paired Successfully"),

    HTTP_RESP_REPAIR_SUCCESS("Success", "Repaired Successfully"),
    HTTP_RESP_OTP_CUSTOM_CHECKED_NO_PAIR_REQUIRED("Pairing-Not-Required", "No Need to pair as Customer Checked"),

    HTTP_RESP_EXCEPTION_LIST_VIP_NO_PAIR_REQUIRED("Pairing-Not-Required", "No Need to pair as Found in Exception list as VIP"),
    HTTP_RESP_EXCEPTION_LIST_NON_VIP_NO_PAIR_REQUIRED("Pairing-Not-Required", "No Need to pair as Found in Exception list as NON VIP"),

    HTTP_RESP_NWL_NO_PAIR_REQUIRED("Pairing-Not-Required", "No Need to pair as Found in Exception list as VIP"),

    HTTP_RESP_ALREADY_PAIRED("Pairing-Not-Required", "Already Paired"),

    HTTP_RESP_OTP_VALIDATION_FAIL("Otp", "Otp Invalid for Request No:<REFERENCE_ID>, left attempt:<OTP_COUNT_LEFT>"),

    HTTP_RESP_INVALID_IMEI_FAIL("Pair-Invalid", "Invalid Imei found"),

    HTTP_RESP_OLD_PAIR_NOT_FOUND_FAIL("Pair-Invalid", "Invalid Request, Pair not exist"),
    HTTP_RESP_DUPLICATE_IMEI_FAIL("Pair-Invalid", "Duplicate imei found"),

    HTTP_RESP_BLACKLIST_IMEI_FAIL("Pair-Invalid", "Blacklist imei found"),
    HTTP_RESP_GREYLIST_IMEI_FAIL("Pair-Invalid", "Greylist imei found"),

    HTTP_RESP_PAIR_COUNT_LIMIT_FAIL("Pair-Invalid", "Pair Count limit Exhausted"),
    HTTP_RESP_PAIR_NOT_FOUND_FAIL("Pair-Invalid", "Pairs Not found "),
    HTTP_RESP_INVALID_IMSI_NOT_FOUND("Pair-Invalid", "Imsi not found in Hlr"),
    HTTP_RESP_OTP_VALIDATION_FAIL_MAX_RETRY("Otp", "Otp validation limit Exhausted for Request No:<REFERENCE_ID>"),

    HTTP_RESP_FAIL_NOT_AVAILABLE_IN_CUSTOM("Pair-Invalid", "Custom Unavailable"),
    HTTP_RESP_REQUEST_ALREADY_PROCESSED("Pair-Invalid", "Request No:<REFERENCE_ID> already processed. Not resending Otp"),
    HTTP_RESP_FAIL_DEVICE_MODELS_ARE_NOT_SAME("Pair-Invalid", "Models Mismatch"),
    HTTP_RESP_FAIL_GSMA_INVALID("Pair-Invalid", "GSMA Invalid"),

    HTTP_RESP_FAIL_DEVICE_TYPES_ARE_NOT_ALLOWED("Pair-Invalid", "Device Type Not allowed for Pair");
    private String httpResp;

    private String description;

    private SmsTag(String httpResp, String description) {
        this.description = description;
        this.httpResp = httpResp;
    }

    public String getDescription() {
        return description;
    }

    public String getHttpResp() {
        return httpResp;
    }
}
