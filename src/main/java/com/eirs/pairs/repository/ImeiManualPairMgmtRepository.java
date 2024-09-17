package com.eirs.pairs.repository;

import com.eirs.pairs.constants.PairRequestTypes;
import com.eirs.pairs.repository.entity.ImeiManualPairMgmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImeiManualPairMgmtRepository extends JpaRepository<ImeiManualPairMgmt, Long> {

    ImeiManualPairMgmt findByRequestId(String requestId);


    @Query(value = "SELECT c from ImeiManualPairMgmt c where ((c.imei1=?1 and c.msisdn1=?2) OR (c.imei2=?1 and c.msisdn2=?2) OR (c.imei3=?1 and c.msisdn3=?2) OR (c.imei4=?1 and c.msisdn4=?2)) and requestType=?3")
    List<ImeiManualPairMgmt> findByImeiAndMsisdn(String actualImei, String msisdn, PairRequestTypes requestType);
}
