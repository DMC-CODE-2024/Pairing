package com.eirs.pairs.service;

import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.BlacklistHis;
import com.eirs.pairs.repository.entity.InvalidImei;

public interface InvalidImeiService {

    Boolean isPresent(String imei);

    Boolean isNotPresent(String imei);

    InvalidImei save(InvalidImei invalidImei);

    Boolean isPresent(String[] actualImeis);
}
