package com.eirs.pairs.orchestrator;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.entity.StagingExceptionList;
import com.eirs.pairs.service.StagingExceptionListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StagingOrchestrator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private StagingExceptionListService stagingExceptionListService;

    public void processForStaging(RecordDataDto recordDataDto) {
        try {
            if (recordDataDto.getIsGsmaValid()) {
                log.info("Not Processing record as GSMA is Valid recordDataDto:{}", recordDataDto);
            } else {
                log.info("Checking In Temp Exception List  PairMode:STAGING recordDataDto:{}", recordDataDto);
                List<StagingExceptionList> list = stagingExceptionListService.getByImei(recordDataDto.getImei());
                if (CollectionUtils.isEmpty(list))
                    addToTempExceptionList(recordDataDto);
                else
                    log.info("Not Adding to StagingExceptionList as Imei is already exist existingData recordDataDto:{} list:{}", list, recordDataDto);
            }
        } catch (Exception e) {
            log.error("Exception while processForStaging :{}", recordDataDto, e);
        }
    }

    private void addToTempExceptionList(RecordDataDto recordDataDto) {
        StagingExceptionList stagingExceptionList = StagingExceptionList.builder().actualImei(recordDataDto.getActualImei()).imei(recordDataDto.getImei()).createdOn(LocalDateTime.now()).operatorName(recordDataDto.getOperatorName()).filename(recordDataDto.getFilename()).edrDatetime(recordDataDto.getDate())
                .requestType("EDR").modeType("Single").msisdn(recordDataDto.getMsisdn()).imsi(recordDataDto.getImsi()).build();
        stagingExceptionList = stagingExceptionListService.save(stagingExceptionList);
        log.info("Added to Temp Exception List stagingExceptionList:{}", stagingExceptionList);
    }

}
