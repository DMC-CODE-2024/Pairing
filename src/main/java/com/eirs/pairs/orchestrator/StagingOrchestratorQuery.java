package com.eirs.pairs.orchestrator;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.constants.StagingQueries;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.repository.entity.StagingExceptionList;
import com.eirs.pairs.service.QueryExecutorService;
import com.eirs.pairs.service.StagingExceptionListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StagingOrchestratorQuery {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private QueryExecutorService queryExecutorService;
    DateTimeFormatter edrTableFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Integer processForStaging(LocalDate localDate) {
        String edrTableDate = localDate.format(edrTableFormat);
        Integer createTableResult = queryExecutorService.executeCreate(StagingQueries.CREATE_STAGING_DROP_TEMP_TABLE_MYSQL);
        if (createTableResult == -1) {
            queryExecutorService.execute(StagingQueries.TRUNCATE_TABLE);
        }
        String insertFromEdrTable = StagingQueries.INSERT_FROM_EDR_TABLE;
        queryExecutorService.execute(insertFromEdrTable.replaceAll(StagingQueries.PARAM_YYYYMMDD, edrTableDate));
        if (createTableResult >= 0) {
            queryExecutorService.execute(StagingQueries.CREATE_INDEX_ON_TEMP_TABLE);
        }
        queryExecutorService.execute(StagingQueries.DELETE_FROM_TEMP_TABLE_EXISTS_IN_TEMP_EXCEPTION);
        Integer recordsInserted = queryExecutorService.execute(StagingQueries.INSERT_INTO_TEMP_EXCEPTION_LIST);
        queryExecutorService.execute(StagingQueries.DROP_TABLE);
        return recordsInserted;
    }
}
