package com.eirs.pairs.service;

import com.eirs.pairs.constants.PairRequestTypes;
import com.eirs.pairs.exception.ResourceNotFoundException;
import com.eirs.pairs.repository.ImeiManualPairMgmtRepository;
import com.eirs.pairs.repository.entity.ImeiManualPairMgmt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ImeiManualPairMgmtServiceImpl implements ImeiManualPairMgmtService {

    @Autowired
    private ImeiManualPairMgmtRepository imeiManualPairMgmtRepository;

    @Override
    public ImeiManualPairMgmt save(ImeiManualPairMgmt imeiManualPairMgmt) {
        return imeiManualPairMgmtRepository.save(imeiManualPairMgmt);
    }

    @Override
    public ImeiManualPairMgmt findByRequestId(String requestId) {
        ImeiManualPairMgmt imeiManualPairMgmt = imeiManualPairMgmtRepository.findByRequestId(requestId);
        if (imeiManualPairMgmt == null)
            throw new ResourceNotFoundException("No Record found for requestId:" + requestId);
        return imeiManualPairMgmt;
    }

    @Override
    public List<ImeiManualPairMgmt> findByImeiAndMsisdn(String actualImei, String msisdn, PairRequestTypes requestTypes) {
        List<ImeiManualPairMgmt> imeiManualPairMgmt = imeiManualPairMgmtRepository.findByImeiAndMsisdn(actualImei, msisdn, requestTypes);
        if (CollectionUtils.isEmpty(imeiManualPairMgmt))
            throw new ResourceNotFoundException("No Record found for actualImei:" + actualImei + ", msisdn:" + msisdn);
        return imeiManualPairMgmt;
    }

}
