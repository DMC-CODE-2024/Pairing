package com.eirs.pairs.service;

import com.eirs.pairs.constants.DeviceSyncOperation;
import com.eirs.pairs.constants.ExceptionListConstants;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.ExceptionListHisRepository;
import com.eirs.pairs.repository.ExceptionListRepository;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.repository.entity.ExceptionListHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExceptionListServiceImpl implements ExceptionListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExceptionListRepository exceptionListRepository;

    @Autowired
    ExceptionListHisRepository exceptionListHisRepository;

    @Override
    public ExceptionList save(ExceptionList exceptionList) {
        try {
            long start = System.currentTimeMillis();
            log.info("Going to save in Exception List : {}", exceptionList);
            exceptionList = exceptionListRepository.save(exceptionList);
            log.info("Saved in to ExceptionList:{} TimeTaken:{}", exceptionList, (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("Exception while adding to ExceptionList exceptionList:{} Error:{}", exceptionList, e.getMessage(), e);
        }
        return exceptionList;
    }

    @Override
    public ExceptionListHis save(ExceptionListHis exceptionListHis) {
        try {
            long start = System.currentTimeMillis();
            log.info("Going to save in Exception List Hist: {}", exceptionListHis);
            exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
            log.info("Saved in to ExceptionListHis:{} TimeTaken:{}", exceptionListHis, (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("Exception while adding to ExceptionListHis List  exceptionListHis:{} Error:{}", exceptionListHis, e.getMessage(), e);
        }
        return exceptionListHis;
    }

    @Override
    public List<ExceptionList> getVIPImsi(String imsi) {
        long start = System.currentTimeMillis();
        log.info("Going to find for VIP in exception list using imsi : {}", imsi);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImsiAndRequestType(imsi, ExceptionListConstants.VIP.name());
        log.info("VIP in exception list using imsi : {}, is : {} TimeTaken:{}", imsi, exceptionLists, (System.currentTimeMillis() - start));
        return exceptionLists;
    }

    @Override
    public List<ExceptionList> getByImeiAndImsi(String imei, String imsi) {
        log.info("Going to check exception list using imei : {} imsi : {}", imei, imsi);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImeiAndImsi(imei, imsi);
        log.info("exception list using imei : {} imsi : {}, is : {}", imei, imsi, exceptionLists);
        return exceptionLists;
    }

    @Override
    public List<ExceptionList> getByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn) {
        log.info("Going to check exception list using imei : {} imsi : {} msisdn:{}", imei, imsi, msisdn);
        List<ExceptionList> exceptionLists = exceptionListRepository.findByImeiAndImsiAndMsisdn(imei, imsi, msisdn);
        log.info("exception list using imei : {} imsi : {}, msisdn:{}, is : {}", imei, imsi, msisdn, exceptionLists);
        return exceptionLists;
    }

    @Override
    public void add(RecordDataDto fileDataDto, String source) {
        long start = System.currentTimeMillis();
        try {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.setImei(fileDataDto.getActualImei().substring(0, 14));
            exceptionList.setActualImei(fileDataDto.getActualImei());
            exceptionList.setImsi(fileDataDto.getImsi());
            exceptionList.setCreatedOn(LocalDateTime.now());
            exceptionList.setMsisdn(fileDataDto.getMsisdn());
            exceptionList.setOperatorId(null);
            exceptionList.setSource(source);
            exceptionList.setTac(fileDataDto.getActualImei().substring(0, 8));
            exceptionList.setOperatorName(fileDataDto.getOperatorName());

            ExceptionListHis exceptionListHis = new ExceptionListHis();
            exceptionListHis.setOperation(DeviceSyncOperation.ADD.ordinal());
            exceptionListHis.setImei(fileDataDto.getActualImei().substring(0, 14));
            exceptionListHis.setActualImei(fileDataDto.getActualImei());
            exceptionListHis.setImsi(fileDataDto.getImsi());
            exceptionListHis.setCreatedOn(LocalDateTime.now());
            exceptionListHis.setMsisdn(fileDataDto.getMsisdn());
            exceptionListHis.setOperatorId(null);
            exceptionListHis.setTac(fileDataDto.getActualImei().substring(0, 8));
            exceptionListHis.setSource(source);
            exceptionListHis.setOperatorName(fileDataDto.getOperatorName());

            exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
            log.info("Added to TimeTaken:{} ExceptionListHis:{} fileDataDto:{}", (System.currentTimeMillis() - start), exceptionListHis, fileDataDto);
            exceptionList = exceptionListRepository.save(exceptionList);
            log.info("Added to TimeTaken:{} ExceptionList:{} fileDataDto:{}", (System.currentTimeMillis() - start), exceptionList, fileDataDto);
        } catch (Exception e) {
            log.error("Exception while adding to Exception List fileDataDto:{} Error:{}", fileDataDto, e.getMessage(), e);
        }
    }

}
