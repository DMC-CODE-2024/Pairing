package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.CustomEntity;
import com.eirs.pairs.repository.entity.MdrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomRepository extends JpaRepository<CustomEntity, Long> {

    CustomEntity findByImei(String imei);

}
