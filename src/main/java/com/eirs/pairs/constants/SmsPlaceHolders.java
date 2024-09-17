package com.eirs.pairs.constants;

public enum SmsPlaceHolders {

    ACTUAL_IMEI("<ACTUAL_IMEI>"), MSISDN("<MSISDN>"), OLD_MSISDN("<OLD_MSISDN>"), PAIR("<PAIR>"), IMSI("<IMSI>"), OPERATOR("<OPERATOR>"), OTP("<OTP>"), REFERENCE_ID("<REFERENCE_ID>"), OTP_COUNT_LEFT("<OTP_COUNT_LEFT>");
    private String placeHolder;

    SmsPlaceHolders(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getPlaceHolder() {
        return this.placeHolder;
    }
}
