package com.logiflow.api.LogiFlow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank(message = "Cidade de origem é obrigatória")
    private String originCity;

    @NotBlank(message = "Cidade de destino é obrigatória")
    private String destinationCity;

    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser maior que zero")
    private Double weight;

    @NotNull(message = "Comprimento é obrigatório")
    @Positive(message = "Comprimento deve ser maior que zero")
    private Double length;

    @NotNull(message = "Largura é obrigatória")
    @Positive(message = "Largura deve ser maior que zero")
    private Double width;

    @NotNull(message = "Altura é obrigatória")
    @Positive(message = "Altura deve ser maior que zero")
    private Double height;
    private Double distanceKm;

    private BigDecimal freightValue;
}