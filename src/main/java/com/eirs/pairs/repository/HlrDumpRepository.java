package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.HlrDumpEntity;
import com.eirs.pairs.repository.entity.Pairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HlrDumpRepository extends JpaRepository<HlrDumpEntity, Long> {

    List<HlrDumpEntity> findByMsisdn(String msisdn);

}
