package com.logiflow.api.LogiFlow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingCode;
    private String originCity;
    private String originState;
    private String destinationCity;
    private String destinationState;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private Double distanceKm;
    private BigDecimal freightValue;
}
