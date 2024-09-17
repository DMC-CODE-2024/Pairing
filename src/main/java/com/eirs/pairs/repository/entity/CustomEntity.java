package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "gdce_data", catalog = "app")
public class CustomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imei")
    private String imei;

    @Column(name = "is_custom_tax_paid")
    private Integer isCustomTaxPaid;
}
