package com.eirs.pairs.service;

import com.eirs.pairs.constants.PairRequestTypes;
import com.eirs.pairs.exception.ResourceNotFoundException;
import com.eirs.pairs.repository.entity.ImeiManualPairMgmt;

import java.util.List;

public interface ImeiManualPairMgmtService {

    ImeiManualPairMgmt save(ImeiManualPairMgmt imeiManualPairMgmt);

    ImeiManualPairMgmt findByRequestId(String requestId) throws ResourceNotFoundException;

    List<ImeiManualPairMgmt> findByImeiAndMsisdn(String actualImei, String msisdn, PairRequestTypes requestTypes) throws ResourceNotFoundException;

}
