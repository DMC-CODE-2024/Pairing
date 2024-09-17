package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.ExceptionListHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface ExceptionListHisRepository extends JpaRepository<ExceptionListHis, Long> {


    @Query("select a from ExceptionListHis a where a.createdOn >= :startDate and a.createdOn < :endDate and a.operatorName = :operatorName")
    public Stream<ExceptionListHis> streamByOperatorNameAndCreatedOnBetween(String operatorName, LocalDateTime startDate, LocalDateTime endDate);
}
