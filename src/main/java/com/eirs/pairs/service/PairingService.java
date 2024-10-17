package com.eirs.pairs.service;

import com.eirs.pairs.constants.GSMAStatus;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.entity.Pairing;

import java.util.List;

public interface PairingService {

    Pairing save(Pairing pairing);

    Pairing delete(Pairing pairing);

    List<Pairing> saveAll(List<Pairing> pairings);

    List<Pairing> getPairsByImeiAndGsmaStatus(String imei, GSMAStatus gsmaStatus);

    List<Pairing> getPairsByImei(String imei);

    List<Pairing> getPairsByActualImei(String actualImei);

    Pairing getPairsByMsisdn(String imei, String msisdn);

    List<Pairing> getPairsByImsi(String imsi);

    Pairing getByImeiAndImsi(String imei, String imsi);

    Pairing getByActualImeiAndImsi(String actualImei, String imsi);

    Pairing getPairsActualImeiByMsisdn(String actualImei, String msisdn);

    List<Pairing> getPairsByMsisdn(String msisdn);

    void addPair(RecordDataDto recordDataDto, GSMAStatus gsmaStatus, int allowedDays);
}
