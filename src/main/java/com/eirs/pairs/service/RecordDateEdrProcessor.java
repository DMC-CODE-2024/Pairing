package com.eirs.pairs.service;

import com.eirs.pairs.config.AppConfig;
import com.eirs.pairs.dto.RecordDataDto;
import com.eirs.pairs.orchestrator.PairingOrchestrator;
import com.eirs.pairs.orchestrator.StagingOrchestrator;
import com.eirs.pairs.orchestrator.StagingOrchestratorQuery;
import com.eirs.pairs.repository.entity.ModuleAuditTrail;
import com.eirs.pairs.utils.DateFormatterConstants;
import com.eirs.pairs.utils.RandomIdGeneratorUtil;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RecordDateEdrProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PairingOrchestrator pairingOrchestrator;

    @Autowired
    private StagingOrchestrator stagingOrchestrator;

    @Autowired
    private StagingOrchestratorQuery stagingOrchestratorQuery;
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    private Boolean isGracePeriodOn;

    AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    ModuleAlertService moduleAlertService;

    @Autowired
    InvalidImeiServiceImpl invalidImeiService;
    final String MODULE_NAME = "Auto Pairing";

    @PostConstruct
    public void init() {
        LocalDate gracePeriodEndDate = systemConfigurationService.getGracePeriodEndDate();
        if (LocalDate.now().isBefore(gracePeriodEndDate) || LocalDate.now().isEqual(gracePeriodEndDate)) {
            isGracePeriodOn = Boolean.TRUE;
        } else {
            isGracePeriodOn = Boolean.FALSE;
        }

        log.info("Running with GracePeriod:{}  From Config gracePeriodEndDate:{}", isGracePeriodOn, gracePeriodEndDate);
    }

    public void processEdr(LocalDate localDate) {
        if (!moduleAuditTrailService.previousDependentModuleExecuted(localDate, appConfig.getDependentFeatureName())) {
            log.info("Process:{} will not execute as already Dependent Module:{} Not Executed for the day {}", appConfig.getFeatureName(), appConfig.getDependentFeatureName(), localDate);
            return;
        }
        if (!moduleAuditTrailService.runProcess(localDate, appConfig.getFeatureName())) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        Long start = System.currentTimeMillis();
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        try {
            if (isGracePeriodOn) {
                Integer recordsInserted = stagingOrchestratorQuery.processForStaging(localDate);
                counter.set(recordsInserted);
            } else {
                invalidImeiService.loadToCache();
                String query = "SELECT id,edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type from app.edr_" + localDate.format(DateFormatterConstants.edrTableFormat) + " where is_paired=0 order by edr_date_time";
                log.info("JDBC TEmplate Selecting Records with Query:[{}]", query);
                jdbcTemplate.setFetchSize(Integer.MIN_VALUE);
                jdbcTemplate.query(query, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        RecordDataDto recordDataDto = new RecordDataDto();
                        recordDataDto.setActualImei(rs.getString("actual_imei"));
                        recordDataDto.setImsi(rs.getString("imsi"));
                        recordDataDto.setDeviceType(rs.getString("device_type") == null ? "" : rs.getString("device_type"));
                        recordDataDto.setMsisdn(rs.getString("msisdn"));
                        recordDataDto.setOperatorName(rs.getString("operator_name"));
                        recordDataDto.setFilename(rs.getString("file_name"));
                        recordDataDto.setIsGsmaValid(rs.getString("is_gsma_valid") == null ? false : (rs.getInt("is_gsma_valid") == 0 ? false : true));
                        recordDataDto.setIsCustomPaid(rs.getString("is_custom_paid") == null ? false : (rs.getInt("is_custom_paid") == 0 ? false : true));
                        recordDataDto.setDate(rs.getTimestamp("edr_date_time").toLocalDateTime());
                        recordDataDto.setTxnId(RandomIdGeneratorUtil.generateRequestId());
                        log.info("Picked Record: {}", recordDataDto);
                        if (StringUtils.isAnyBlank(recordDataDto.getActualImei(), recordDataDto.getImsi(), recordDataDto.getOperatorName())) {
                            log.info("Not Processing as Actual Imei , Imsi or Operator Is Blank recordDataDto:{}", recordDataDto);
                        } else {
                            if (recordDataDto.getActualImei().length() < 14) {
                                recordDataDto.setImei(recordDataDto.getActualImei());
                            } else {
                                recordDataDto.setImei(recordDataDto.getActualImei().substring(0, 14));
                            }
                            if (recordDataDto.getImsi().startsWith("456")) {
                                log.info("Processing recordDataDto:{}", recordDataDto);
                            /*if (isGracePeriodOn)
                                stagingOrchestrator.processForStaging(recordDataDto);
                            else*/
                                pairingOrchestrator.processForPairing(recordDataDto);
                            } else {
                                log.info("Not Processing record as Imsi is not starting with 456 recordDataDto:{}", recordDataDto);
                            }
                            log.info("Record Read recordDataDto:{}", recordDataDto);
                        }
                        counter.getAndIncrement();
                    }
                });
                log.info("Total time Taken {}", (System.currentTimeMillis() - start));
            }
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            log.error("Error while Processing GracePeriod:{} Error:{} ", isGracePeriodOn, e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(counter.get());
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }
}
