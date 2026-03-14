package com.logiflow.api.LogiFlow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ShipmentRequestDTO {

    @NotBlank(message = "Cidade de origem é obrigatória")
    private String originCity;

    @NotBlank(message = "Estado de origem é obrigatório")
    private String originState;

    @NotBlank(message = "Cidade de destino é obrigatória")
    private String destinationCity;

    @NotBlank(message = "Estado de destino é obrigatório")
    private String destinationState;

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
}
