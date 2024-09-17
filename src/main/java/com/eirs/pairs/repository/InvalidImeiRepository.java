package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.InvalidImei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvalidImeiRepository extends JpaRepository<InvalidImei, Long> {

    Optional<InvalidImei> findByImei(String imei);

}
