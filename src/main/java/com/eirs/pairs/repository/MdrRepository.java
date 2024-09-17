package com.eirs.pairs.repository;

import com.eirs.pairs.constants.GSMAStatus;
import com.eirs.pairs.repository.entity.MdrEntity;
import com.eirs.pairs.repository.entity.Pairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MdrRepository extends JpaRepository<MdrEntity, Long> {

    MdrEntity findByTac(String tac);

}
