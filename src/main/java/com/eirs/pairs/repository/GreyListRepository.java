package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.GreyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GreyListRepository extends JpaRepository<GreyList, Long> {
    List<GreyList> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);

    List<GreyList> findByActualImei(String actualImei);
}
