package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;


@Repository
public interface BlackListRepository extends JpaRepository<Blacklist, Long> {

    List<Blacklist> findByImei(String imei);

    List<Blacklist> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);

    List<Blacklist> findByActualImei(String actualImei);
}
