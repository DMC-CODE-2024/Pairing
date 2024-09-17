package com.eirs.pairs.service;

import com.eirs.pairs.repository.GuiAuditTrailRepository;
import com.eirs.pairs.repository.GuiPortalAccessLogRepository;
import com.eirs.pairs.repository.entity.GuiAuditTrail;
import com.eirs.pairs.repository.entity.GuiPortalAccessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PortalAccessAuditTrailService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    GuiPortalAccessLogRepository guiPortalAccessLogRepository;

    @Autowired
    GuiAuditTrailRepository guiAuditTrailRepository;

    public void captureAudit(String ip, String userAgent, String txnId, String subFeature) {
        String browser = getBrowser(userAgent);
        log.info("Saving Audit for  ip:{} browser:{} txnId:{} userAgent:{} ", ip, browser, txnId, userAgent);
        try {
            GuiAuditTrail auditTrail = new GuiAuditTrail();
            auditTrail.setFeatureName("Pairing Portal");
            auditTrail.setSubFeature(subFeature);
            auditTrail.setFeatureId(1000002);
            auditTrail.setPublicIp(ip);
            auditTrail.setBrowser(browser);
            auditTrail.setUserId(1000002);
            auditTrail.setUserName("Public Portal");
            auditTrail.setUserType("NA");
            auditTrail.setRoleType("NA");
            auditTrail.setCreatedOn(LocalDateTime.now());
            auditTrail.setModifiedOn(LocalDateTime.now());
            auditTrail.setTxnId(txnId);
            guiAuditTrailRepository.save(auditTrail);
        } catch (Exception e) {
            log.error("Error while Saving GuiAuditTrail for  ip:{} browser:{} txnId:{} userAgent:{} ", ip, browser, txnId, userAgent);
        }
        try {
            GuiPortalAccessLog header = new GuiPortalAccessLog();
            header.setBrowser(browser);
            header.setPublicIp(ip);
            header.setUserAgent(userAgent);
            header.setUsername("Public Portal");
            header.setCreatedOn(LocalDateTime.now());
            header.setModifiedOn(LocalDateTime.now());
            guiPortalAccessLogRepository.save(header);
        } catch (Exception e) {
            log.error("Error while Saving GuiPortalAccessLog for  ip:{} browser:{} txnId:{} userAgent:{} ", ip, browser, txnId, userAgent);
        }

    }

    private String getBrowser(String userAgent) {
        String browser = "";
        String version = "";
        Integer startLen = 0;
        Integer endLen = 0;
        if (userAgent.toLowerCase().indexOf("msie") != -1) {
            browser = "IE";
            startLen = userAgent.toLowerCase().indexOf("msie");
            endLen = userAgent.indexOf(";", startLen);
            version = userAgent.substring(startLen + 5, endLen);
        } else if (userAgent.toLowerCase().indexOf("trident/7") != -1) {
            browser = "IE";
            startLen = userAgent.toLowerCase().indexOf("rv:") + 3;
            endLen = userAgent.indexOf(")", startLen);
            version = userAgent.substring(startLen, endLen);
        } else if (userAgent.toLowerCase().indexOf("chrome") != -1) {
            browser = "CHROME";
            startLen = userAgent.toLowerCase().indexOf("chrome") + 7;
            endLen = userAgent.indexOf(" ", startLen);
            version = userAgent.substring(startLen, endLen);
        } else if (userAgent.toLowerCase().indexOf("firefox") != -1) {
            browser = "FIREFOX";
            startLen = userAgent.toLowerCase().indexOf("firefox") + 8;
            endLen = userAgent.length();
            version = userAgent.substring(startLen, endLen);

        } else if (userAgent.toLowerCase().indexOf("safari") != -1) {
            browser = "SAFARI";
            startLen = userAgent.toLowerCase().indexOf("version") + 8;
            endLen = userAgent.indexOf(" ", startLen);
            version = userAgent.substring(startLen, endLen);
        } else if (userAgent.toLowerCase().indexOf("opera") != -1) {
            browser = "OPERA";
            startLen = userAgent.toLowerCase().indexOf("opera") + 6;
            endLen = userAgent.length();
            version = userAgent.substring(startLen, endLen);
        } else {
            browser = "OTHER";
        }

        return browser + "_" + version;
    }
}
