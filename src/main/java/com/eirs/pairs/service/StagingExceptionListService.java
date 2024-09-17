package com.eirs.pairs.service;

import com.eirs.pairs.repository.entity.StagingExceptionList;

import java.util.List;

public interface StagingExceptionListService {

    StagingExceptionList save(StagingExceptionList stagingExceptionList);

    List<StagingExceptionList> getByImei(String imei);
}
