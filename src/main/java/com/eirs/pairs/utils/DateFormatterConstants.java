package com.eirs.pairs.utils;

import java.time.format.DateTimeFormatter;

public interface DateFormatterConstants {
    DateTimeFormatter requestIdDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    DateTimeFormatter edrTableFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    DateTimeFormatter gracePeriodEndDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
