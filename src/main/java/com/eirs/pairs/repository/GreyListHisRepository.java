package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.GreyListHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface GreyListHisRepository extends JpaRepository<GreyListHis, Long> {


    @Query("select a from GreyListHis a where a.createdOn >= :startDate and a.createdOn < :endDate and a.operatorName = :operatorName")
    public Stream<GreyListHis> streamByOperatorNameAndCreatedOnBetween(String operatorName, LocalDateTime startDate, LocalDateTime endDate);
}
