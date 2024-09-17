package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.Blacklist;
import com.eirs.pairs.repository.entity.NationalWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NationalWhitelistRepository extends JpaRepository<NationalWhitelist, Long> {
    NationalWhitelist findByImeiAndGdceImeiStatusGreaterThan(String imei, Integer gdceImeiStatus);

}
