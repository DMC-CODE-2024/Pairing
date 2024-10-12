package com.eirs.pairs.service;

import com.eirs.pairs.repository.DuplicateRepository;
import com.eirs.pairs.repository.entity.Duplicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DuplicateServiceImpl implements DuplicateService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DuplicateRepository duplicateRepository;

    public Boolean isAvailable(String imei, String imsi) {
        log.info("Finding in duplicate table using imei : {} and imsi : {}", imei, imsi);
        Duplicate duplicate = duplicateRepository.findByImeiAndImsi(imei, imsi);
        log.info("Get Duplicate for Imei:{} Imsi:{} Duplicate:{}", imei, imsi, duplicate);
        if (duplicate == null)
            return false;
        return true;
    }

    public Boolean isAvailable(String imei) {
        log.info("Finding in duplicate table using imei : {}", imei);
        List<Duplicate> duplicates = duplicateRepository.findByImei(imei);
        log.info("Get Duplicate for Imei:{} Duplicate:{}", imei, duplicates);
        if (CollectionUtils.isEmpty(duplicates))
            return false;
        return true;
    }

    @Override
    public Boolean isNotAvailable(String imei) {
        return !isAvailable(imei);
    }

    @Override
    public Duplicate get(String imei, String imsi) {
        long start = System.currentTimeMillis();
        log.info("Finding in duplicate table using imei : {} and imsi : {}", imei, imsi);
        Duplicate duplicate = duplicateRepository.findByImeiAndImsi(imei, imsi);
        log.info("Get Duplicate for Imei:{} Imsi:{} Duplicate:{} TimeTaken:{}", imei, imsi, duplicate, (System.currentTimeMillis() - start));
        return duplicate;
    }

    @Override
    public Duplicate save(Duplicate duplicate) {
        try {
            long start = System.currentTimeMillis();
            duplicate = duplicateRepository.save(duplicate);
            log.info("Saved to Duplicate:{} TimeTaken:{}", duplicate, (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("Exception while saving to Duplicate:{} Error:{}", duplicate, e.getMessage(), e);
        }
        return duplicate;
    }
}
