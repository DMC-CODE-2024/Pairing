package com.eirs.pairs.service;

import com.eirs.pairs.constants.DeviceSyncOperation;
import com.eirs.pairs.constants.ExceptionListConstants;
import com.eirs.pairs.constants.PairMode;
import com.eirs.pairs.dto.PairDto;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.dto.ValidateOtpRequestDto;
import com.eirs.pairs.repository.ExceptionListHisRepository;
import com.eirs.pairs.repository.ExceptionListRepository;
import com.eirs.pairs.repository.entity.ExceptionList;
import com.eirs.pairs.repository.entity.ExceptionListHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
    public void add(ValidateOtpRequestDto validateOtpRequestDto, String source) {
        try {
            long start = System.currentTimeMillis();
            log.info("Adding In Exception List validateOtpRequestDto:{} ", validateOtpRequestDto);
            List<ExceptionList> exceptionLists = new ArrayList<>();
            List<ExceptionListHis> exceptionListHiss = new ArrayList<>();
            for (PairDto pairDto : validateOtpRequestDto.getPairs()) {
                ExceptionList exceptionList = new ExceptionList();
                exceptionList.setOperatorName(pairDto.getOperator());
                exceptionList.setActualImei(pairDto.getActualImei());
                exceptionList.setImsi(pairDto.getImsi());
                exceptionList.setMsisdn(pairDto.getMsisdn());
                exceptionList.setCreatedOn(LocalDateTime.now());
                exceptionList.setTxnId(validateOtpRequestDto.getRequestId());
                exceptionList.setImei(pairDto.getImei());
                exceptionList.setSource(source);
                exceptionLists.add(exceptionList);

                ExceptionListHis exceptionListHis = new ExceptionListHis();
                exceptionListHis.setOperatorName(pairDto.getOperator());
                exceptionListHis.setImsi(pairDto.getImsi());
                exceptionListHis.setActualImei(pairDto.getActualImei());
                exceptionListHis.setMsisdn(pairDto.getMsisdn());
                exceptionListHis.setCreatedOn(LocalDateTime.now());
                exceptionListHis.setOperation(DeviceSyncOperation.ADD.ordinal());
                exceptionListHis.setImei(pairDto.getImei());
                exceptionListHis.setSource(source);
                exceptionListHis.setTxnId(validateOtpRequestDto.getRequestId());
                exceptionListHiss.add(exceptionListHis);
            }
            exceptionListHisRepository.saveAll(exceptionListHiss);
            log.info("Added in Exception History TimeTaken:{} exceptionListHis:{} ", (System.currentTimeMillis() - start), exceptionListHiss);
            exceptionListRepository.saveAll(exceptionLists);
            log.info("Added in Exception List TimeTaken:{} exceptionList:{}", (System.currentTimeMillis() - start), exceptionLists);
        } catch (Exception e) {
            log.error("Exception while adding to Exception List source:{} validateOtpRequestDto:{} Error:{}", source, validateOtpRequestDto, e.getMessage(), e);
        }
    }

    @Override
    public void add(PairDto pairDto, String txnId, String source) {
        try {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.setOperatorName(pairDto.getOperator());
            exceptionList.setActualImei(pairDto.getActualImei());
            exceptionList.setImsi(pairDto.getImsi());
            exceptionList.setMsisdn(pairDto.getMsisdn());
            exceptionList.setCreatedOn(LocalDateTime.now());
            exceptionList.setTxnId(txnId);
            exceptionList.setImei(pairDto.getImei());
            exceptionList.setSource(source);

            ExceptionListHis exceptionListHis = new ExceptionListHis();
            exceptionListHis.setOperatorName(pairDto.getOperator());
            exceptionListHis.setImsi(pairDto.getImsi());
            exceptionListHis.setActualImei(pairDto.getActualImei());
            exceptionListHis.setMsisdn(pairDto.getMsisdn());
            exceptionListHis.setCreatedOn(LocalDateTime.now());
            exceptionListHis.setOperation(DeviceSyncOperation.ADD.ordinal());
            exceptionListHis.setImei(pairDto.getImei());
            exceptionListHis.setSource(source);
            exceptionListHis.setTxnId(txnId);
            exceptionListHisRepository.save(exceptionListHis);
            log.info("Added in Exception History exceptionListHis:{} ", exceptionListHis);
            exceptionListRepository.save(exceptionList);
            log.info("Added in Exception List exceptionList:{}", exceptionList);
        } catch (Exception e) {
            log.error("Exception while adding to Exception List txnId:{} pairDto:{} Error:{}", txnId, pairDto, e.getMessage(), e);
        }
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

    @Override
    public void delete(PairDto pairDto, String source, List<ExceptionList> exceptionLists) {
        try {
            long start = System.currentTimeMillis();
            log.info("Deleting pairDto:{} from Exception List", pairDto);
            exceptionListRepository.deleteAll(exceptionLists);
            log.info("Deleted TimeTaken:{} pairDto:{} from Exception List {}", (System.currentTimeMillis() - start), pairDto, exceptionLists);
            if (CollectionUtils.isEmpty(exceptionLists)) {
                return;
            }
            start = System.currentTimeMillis();
            ExceptionList exceptionList = exceptionLists.get(0);
            ExceptionListHis exceptionListHis = new ExceptionListHis();
            exceptionListHis.setOperatorName(exceptionList.getOperatorName());
            exceptionListHis.setImsi(exceptionList.getImsi());
            exceptionListHis.setMsisdn(exceptionList.getMsisdn());
            exceptionListHis.setCreatedOn(LocalDateTime.now());
            exceptionListHis.setOperation(DeviceSyncOperation.DELETE.ordinal());
            exceptionListHis.setImei(exceptionList.getImei());
            exceptionListHis.setActualImei(exceptionList.getActualImei());
            exceptionListHis.setTxnId(exceptionList.getTxnId());
            exceptionListHis.setSource(source);
            exceptionListHis = exceptionListHisRepository.save(exceptionListHis);
            log.info("Added in Exception History TimeTaken:{} exceptionListHis:{} ", (System.currentTimeMillis() - start), exceptionListHis);
        } catch (Exception e) {
            log.error("Exception while deleting from Exception List pairDto:{} source:{} Error:{}", pairDto, source, e.getMessage(), e);
        }
    }
}
