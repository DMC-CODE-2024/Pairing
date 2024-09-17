package com.eirs.pairs.service;

import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.BlacklistHis;

import java.util.List;

public interface BlackListService {

    Blacklist save(Blacklist blacklist);

    List<Blacklist> findByImei(String imei);

    List<Blacklist> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);

    BlacklistHis save(BlacklistHis blacklistHis);

    void add(RecordDataDto fileDataDto);

    void addAndUpdate(RecordDataDto fileDataDto);
}
