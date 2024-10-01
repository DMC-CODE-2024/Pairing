package com.eirs.pairs.service;

import com.eirs.pairs.constants.*;
import com.eirs.pairs.dto.NotificationDetailsDto;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.PairingRepository;
import com.eirs.pairs.repository.entity.InvalidImei;
import com.eirs.pairs.repository.entity.Pairing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PairingServiceImpl implements PairingService {
    @Autowired
    private PairingRepository pairingRepository;

    @Override
    public Pairing save(Pairing pairing) {
        long start = System.currentTimeMillis();
        log.info("Going to Add for Pair : {} is saved ", pairing);
        Pairing pair = pairingRepository.save(pairing);
        log.info("Pair : {} is saved TimeTaken:{}", pair, (System.currentTimeMillis() - start));
        return pair;
    }

    @Override
    public Pairing delete(Pairing pairing) {
        long start = System.currentTimeMillis();
        log.info("Going to Delete Pair : {} ", pairing);
        Pairing pair = pairingRepository.save(pairing);
        log.info("Pair : {} is Deleted TimeTaken:{}", pair, (System.currentTimeMillis() - start));
        return pair;
    }

    @Override
    @Transactional
    public List<Pairing> saveAll(List<Pairing> pairings) {
        long start = System.currentTimeMillis();
        log.info("Going to Add for pairings : {} is saved ", pairings);
        pairings = pairingRepository.saveAll(pairings);
        log.info("Pair : {} is saved TimeTaken:{}", pairings, (System.currentTimeMillis() - start));
        return pairings;
    }

    @Override
    public List<Pairing> getPairsByImeiAndGsmaStatus(String imei, GSMAStatus gsmaStatus) {
        long start = System.currentTimeMillis();
        log.info("Find in Pairing table imei : {}, gsma status : {}", imei, gsmaStatus);
        List<Pairing> pairs = pairingRepository.findByImeiAndGsmaStatus(imei, gsmaStatus);
        log.info("pairs found by IMEI : {} gsmaStatus:{} count : {} TimeTaken:{}", imei, gsmaStatus, pairs.size(), (System.currentTimeMillis() - start));
        return pairs;
    }

    @Override
    public List<Pairing> getPairsByImei(String imei) {
        long start = System.currentTimeMillis();
        log.info("Find in Pairing table imei : {}", imei);
        List<Pairing> pairs = pairingRepository.findByImei(imei);
        log.info("pairs found by IMEI : {} count : {} TimeTaken:{}", imei, pairs.size(), (System.currentTimeMillis() - start));
        return pairs;
    }

    @Override
    public List<Pairing> getPairsByActualImei(String actualImei) {
        long start = System.currentTimeMillis();
        log.info("Find in pairing table  actual imei : {}", actualImei);
        List<Pairing> pairs = pairingRepository.findByActualImei(actualImei);
        log.info("pairs found by actualImei : {} count : {} TimeTaken:{}", actualImei, pairs.size(), (System.currentTimeMillis() - start));
        return pairs;
    }

    @Override
    public Pairing getPairsByMsisdn(String imei, String msisdn) {
        long start = System.currentTimeMillis();
        log.info("Find in Pairing table imei : {}, msisdn : {}", imei, msisdn);
        Pairing pairing = pairingRepository.findByImeiAndMsisdn(imei, msisdn);
        if (pairing == null) {
            log.info("Pair not found by IMEI:{} msisdn:{} TimeTaken:{}", imei, msisdn, (System.currentTimeMillis() - start));
            return null;
        } else {
            log.info("Pair found by IMEI:{} msisdn:{} pairing:{} TimeTaken:{}", imei, msisdn, pairing, (System.currentTimeMillis() - start));
            return pairing;
        }
    }

    @Override
    public List<Pairing> getPairsByImsi(String imsi) {
        long start = System.currentTimeMillis();
        log.info("Find in PPairing table imsi : {}", imsi);
        List<Pairing> pairs = pairingRepository.findByImsi(imsi);
        log.info("pairs found by imsi : {} count : {} TimeTaken:{}", imsi, pairs.size(), (System.currentTimeMillis() - start));
        return pairs;
    }

    @Override
    public Pairing getByImeiAndImsi(String imei, String imsi) {
        long start = System.currentTimeMillis();
        log.info("Find in pairing table imei : {} , imsi : {}", imei, imsi);
        Pairing pairing = pairingRepository.findByImeiAndImsi(imei, imsi);
        if (pairing == null) {
            log.info("Pair not found by IMEI:{} IMSI:{} TimeTaken:{}", imei, imsi, (System.currentTimeMillis() - start));
            return null;
        } else {
            log.info("Pair found by IMEI:{} IMSI:{} TimeTaken:{}", imei, imsi, (System.currentTimeMillis() - start));
            return pairing;
        }
    }

    @Override
    public Pairing getByActualImeiAndImsi(String actualImei, String imsi) {
        long start = System.currentTimeMillis();
        log.info("Find in Pairing table actual imei: {}, imsi : {}", actualImei, imsi);
        Pairing pairing = pairingRepository.findByActualImeiAndImsi(actualImei, imsi);
        if (pairing == null) {
            log.info("Pair not found by actualImei:{} IMSI:{} TimeTaken:{}", actualImei, imsi, (System.currentTimeMillis() - start));
            return null;
        } else {
            log.info("Pair found by actualImei:{} IMSI:{} TimeTaken:{}", actualImei, imsi, (System.currentTimeMillis() - start));
            return pairing;
        }
    }

    @Override
    public Pairing getPairsActualImeiByMsisdn(String actualImei, String msisdn) {
        log.info("Find in Pairing table actual imei : {}, msisdn : {}", actualImei, msisdn);
        Pairing pairing = pairingRepository.findByActualImeiAndMsisdn(actualImei, msisdn);
        if (pairing == null) {
            log.info("Pair not found by actualImei:{} msisdn:{}", actualImei, msisdn);
            return null;
        } else {
            log.info("Pair found by actualImei:{} msisdn:{}", actualImei, msisdn);
            return pairing;
        }
    }

    @Override
    public List<Pairing> getPairsByMsisdn(String msisdn) {
        long start = System.currentTimeMillis();
        log.info("Find in Pairing table msisdn : {}", msisdn);
        List<Pairing> pairs = pairingRepository.findByMsisdn(msisdn);
        log.info("pairs found by msisdn : {} count : {} TimeTaken:{}", msisdn, pairs.size(), (System.currentTimeMillis() - start));
        return pairs;
    }

    @Override
    public void addPair(RecordDataDto recordDataDto, GSMAStatus gsmaStatus, int allowedDays) {
        List<Pairing> pairings = getPairsByImei(recordDataDto.getImei());
        Optional<Pairing> pairOptional = pairings.stream().filter(pair -> StringUtils.equals(pair.getImsi(), recordDataDto.getImsi())).findFirst();
        if (pairOptional.isPresent()) {
            log.info("Already Paired with Imei PairMode:PAIRING recordDataDto:{}", recordDataDto);
            if (pairOptional.get().getRecordTime() == null) {
                log.info("Updating EdrDateTime for pair:{}", pairOptional.get());
                pairOptional.get().setRecordTime(recordDataDto.getDate());
                save(pairOptional.get());
                log.info("Updated EdrDateTime for pair:{}", pairOptional.get());
            }
        } else {
            log.info("As GSMA is Invalid OR Invalid IMEI OR Found in Duplicate so Adding pair {}", recordDataDto);
            Pairing paring = Pairing.builder().pairingDate(LocalDateTime.now()).imsi(recordDataDto.getImsi()).gsmaStatus(gsmaStatus)
                    .recordTime(recordDataDto.getDate()).filename(recordDataDto.getFilename()).allowedDays(allowedDays).imei(recordDataDto.getImei()).actualImei(recordDataDto.getActualImei())
                    .imsi(recordDataDto.getImsi()).txnId(recordDataDto.getTxnId()).msisdn(recordDataDto.getMsisdn()).operator(recordDataDto.getOperatorName()).pairMode(PairMode.AUTO).build();
            if (allowedDays > 0)
                paring.setExpiryDate(LocalDateTime.now().plusDays(allowedDays));
            save(paring);
        }
    }
}
