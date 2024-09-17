package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "mobile_device_repository", catalog = "app")
public class MdrEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id")
    private String tac;

    @Column(name = "model_name")
    private String model;

    @Column(name = "device_type")
    private String deviceType;
}
