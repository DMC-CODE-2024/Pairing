package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.OperatorSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorSeriesRepository extends JpaRepository<OperatorSeries, Long> {

    @Query("select a from OperatorSeries a where a.seriesStart <= :series and a.seriesEnd >= :series and seriesType='msisdn'")
    Optional<OperatorSeries> findAllWithCreationDateTimeBefore(@Param("series") Integer series);

}
