package com.eirs.pairs.alerts;

import com.eirs.pairs.alerts.constants.AlertIds;
import com.eirs.pairs.alerts.constants.AlertMessagePlaceholders;

import java.util.Map;

public interface AlertService {

    void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
}
