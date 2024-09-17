package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.StagingExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StagingExceptionListRepository extends JpaRepository<StagingExceptionList, Long> {

    List<StagingExceptionList> findByImei(String imei);
}
