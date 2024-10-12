package com.eirs.pairs.service;

import com.eirs.pairs.repository.InvalidImeiRepository;
import com.eirs.pairs.repository.entity.InvalidImei;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InvalidImeiServiceImpl implements InvalidImeiService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvalidImeiRepository invalidImeiRepository;

    private Map<String, Boolean> cache = new HashMap<>();

    public void loadToCache() {
        log.info("Started Loading Invalid Imei Data to cache");
        try {
            invalidImeiRepository.findAll().stream().parallel().forEach(invalidImei -> cache.put(invalidImei.getImei(), Boolean.TRUE));
        } catch (Exception e) {
            log.error("Error While loading Invalid Imei data to Cache {}", e.getMessage(), e);
        }
        log.info("Loaded size:{} of invalid Data into Cache ", cache.size());
    }

    public Boolean isPresent(String imei) {
        long start = System.currentTimeMillis();
        log.info("Checking in InvalidImei for imei:{}", imei);
        Optional<InvalidImei> invalidImei = invalidImeiRepository.findByImei(imei);
        log.info("Found in InvalidImei for imei:{} found:{} TimeTaken:{}", imei, invalidImei.isPresent(), (System.currentTimeMillis() - start));
        return invalidImei.isPresent();
    }

    @Override
    public Boolean isPresentFromCache(String imei) {
        return BooleanUtils.isTrue(cache.get(imei));
    }

    @Override
    public Boolean isNotPresent(String imei) {
        return !isPresentFromCache(imei);
    }

    public InvalidImei save(InvalidImei invalidImei) {
        if (!isPresentFromCache(invalidImei.getImei())) {
            long start = System.currentTimeMillis();
            log.info("Going to save into invalidImei:{}", invalidImei);
            invalidImei = invalidImeiRepository.save(invalidImei);
            cache.put(invalidImei.getImei(), Boolean.TRUE);
            log.info("Saved into invalidImei:{} TimeTaken:{}", invalidImei, (System.currentTimeMillis() - start));
        }
        return invalidImei;
    }

    @Override
    public Boolean isPresent(String[] imeis) {
        if (imeis != null) {
            for (String imei : imeis) {
                if (imei != null) {
                    Optional<InvalidImei> optional = invalidImeiRepository.findByImei(imei);
                    if (optional.isPresent())
                        return true;
                }
            }
        }
        return false;
    }
}
