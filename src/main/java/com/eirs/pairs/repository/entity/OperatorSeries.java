package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "operator_series", catalog = "app")
public class OperatorSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_start ")
    private Integer seriesStart;

    @Column(name = "series_end")
    private Integer seriesEnd;

    @Column(name = "series_type")
    private String seriesType;

    @Column(name = "operator_name")
    private String operatorName;
}
