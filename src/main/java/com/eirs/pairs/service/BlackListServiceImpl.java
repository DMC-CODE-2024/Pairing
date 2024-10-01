package com.eirs.pairs.service;

import com.eirs.pairs.constants.DeviceSyncOperation;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.BlackListHisRepository;
import com.eirs.pairs.repository.BlackListRepository;
import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.BlacklistHis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlackListServiceImpl implements BlackListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlackListHisRepository blacklistHisRepository;

    @Autowired
    BlackListRepository blacklistRepository;

    private final String PAIRING = "PAIRING";

    public Blacklist save(Blacklist blacklist) {
        long start = System.currentTimeMillis();
        log.info("going to save in BlackList : {}", blacklist);
        blacklist = blacklistRepository.save(blacklist);
        log.info("Saved in to Blacklist:{} TimeTaken:{}", blacklist, (System.currentTimeMillis() - start));
        return blacklist;
    }

    @Override
    public List<Blacklist> findByImei(String imei) {
        long start = System.currentTimeMillis();
        log.info("going to find BlackList imei:{}", imei);
        List<Blacklist> blacklists = blacklistRepository.findByImei(imei);
        log.info("Found Blacklist for Imei:{} blacklists:{} TimeTaken:{}", imei, blacklists, (System.currentTimeMillis() - start));
        return blacklists;
    }

    @Override
    public List<Blacklist> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn) {
        long start = System.currentTimeMillis();
        log.info("Going to check Blacklist using imei : {} imsi : {} msisdn:{}", imei, imsi, msisdn);
        List<Blacklist> blacklists = blacklistRepository.findByImeiAndImsiAndMsisdn(imei, imsi, msisdn);
        log.info("Blacklist using imei : {} imsi : {}, msisdn:{}, is : {} TimeTaken:{}", imei, imsi, msisdn, blacklists, (System.currentTimeMillis() - start));
        return blacklists;
    }

    public BlacklistHis save(BlacklistHis blacklistHis) {
        long start = System.currentTimeMillis();
        log.info("going to save in BlackListHis : {}", blacklistHis);
        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Saved in to BlacklistHis:{} TimeTaken:{}", blacklistHis, (System.currentTimeMillis() - start));
        return blacklistHis;
    }

    @Override
    public void add(RecordDataDto fileDataDto) {
        BlacklistHis blacklistHis = new BlacklistHis();
        blacklistHis.setOperation(DeviceSyncOperation.ADD.ordinal());
        blacklistHis.setImei(fileDataDto.getActualImei().substring(0, 14));
        blacklistHis.setActualImei(fileDataDto.getActualImei());
//        blacklistHis.setImsi(fileDataDto.getImsi());
        blacklistHis.setCreatedOn(LocalDateTime.now());
        blacklistHis.setMsisdn(null);
        blacklistHis.setOperatorId(null);
        blacklistHis.setOperatorName(null);
        blacklistHis.setSource(PAIRING);
        blacklistHis.setTac(fileDataDto.getActualImei().substring(0, 8));

        Blacklist blacklist = new Blacklist();
        blacklist.setImei(fileDataDto.getActualImei().substring(0, 14));
        blacklist.setActualImei(fileDataDto.getActualImei());
//        blacklist.setImsi(fileDataDto.getImsi());
        blacklist.setCreatedOn(LocalDateTime.now());
        blacklist.setMsisdn(null);
        blacklist.setOperatorId(null);
        blacklist.setOperatorName(null);
        blacklist.setSource(PAIRING);
        blacklist.setTac(fileDataDto.getActualImei().substring(0, 8));
//      blacklist.setOperatorName(fileDataDto.getOperatorName());

        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Added to BlackListHis:{} fileDataDto:{}", blacklistHis, fileDataDto);
        blacklist = blacklistRepository.save(blacklist);
        log.info("Added to BlackList:{} fileDataDto:{}", blacklist, fileDataDto);
    }

    @Override
    public void addAndUpdate(RecordDataDto recordDataDto) {
        List<Blacklist> blacklists = blacklistRepository.findByImei(recordDataDto.getImei());
        log.info("Checked for Blacklist recordDataDto:{} blacklists:{}", recordDataDto, blacklists);
        if (CollectionUtils.isEmpty(blacklists)) {
            add(recordDataDto);
        } else {
            for (Blacklist blacklist : blacklists) {
                if (StringUtils.isBlank(blacklist.getSource())) {
                    blacklist.setSource(PAIRING);
                } else {
                    if (blacklist.getSource().contains(PAIRING)) {
                        log.error("Blacklist already exist with Source:PAIRING for recordDataDto:{}", recordDataDto);
                    } else {
                        blacklist.setSource(blacklist.getSource() + "," + PAIRING);
                    }
                }
            }
            log.info("Going to update blacklists:{}", blacklists);
            blacklistRepository.saveAll(blacklists);
            log.info("Updated for Source for blacklists:{}", blacklists);
        }
    }
}
