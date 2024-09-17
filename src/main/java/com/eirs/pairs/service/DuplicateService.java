package com.eirs.pairs.service;

import com.eirs.pairs.repository.entity.Duplicate;

public interface DuplicateService {

    Boolean isAvailable(String imei, String imsi);

    Boolean isAvailable(String imei);

    Boolean isNotAvailable(String imei);

    Duplicate get(String imei, String imsi);

    Duplicate save(Duplicate duplicate);
}
