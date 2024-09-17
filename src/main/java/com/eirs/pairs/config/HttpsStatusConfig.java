package com.eirs.pairs.config;

import com.eirs.pairs.constants.SmsTag;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "http-response")
public class HttpsStatusConfig {

    private Map<String, String> statuses;

    public String getStatus(SmsTag smsTag) {
        String msg = statuses.get(smsTag.name());
        if (msg == null)
            return smsTag.getHttpResp();
        return msg;
    }

}
