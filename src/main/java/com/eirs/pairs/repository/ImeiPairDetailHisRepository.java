package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.ImeiPairDetailHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImeiPairDetailHisRepository extends JpaRepository<ImeiPairDetailHis, Long> {

}
