package com.eirs.pairs.service;

import com.eirs.pairs.repository.StagingExceptionListRepository;
import com.eirs.pairs.repository.entity.StagingExceptionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StagingExceptionListServiceImpl implements StagingExceptionListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StagingExceptionListRepository stagingExceptionListRepository;


    @Override
    public StagingExceptionList save(StagingExceptionList stagingExceptionList) {
        log.info("Going to save in temp Exception list : {}",stagingExceptionList);
        stagingExceptionList = stagingExceptionListRepository.save(stagingExceptionList);
        log.info("Saved in to StagingExceptionList:{}", stagingExceptionList);
        return stagingExceptionList;
    }

    @Override
    public List<StagingExceptionList> getByImei(String imei) {
        log.info("Find in temp exception list imie : {}",imei);
        List<StagingExceptionList> tempException = stagingExceptionListRepository.findByImei(imei);
        log.info("Found in temp exception imei : {} , data : {}",imei,tempException);
        return tempException;
    }
}
