package com.logiflow.api.LogiFlow.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class ShipmentResponseDTO {
    private Long id;
    private String trackingCode;
    private String originCity;      // Adicionado
    private String destinationCity; // Adicionado
    private Double distanceKm;      // Adicionado
    private BigDecimal freightValue;

    public ShipmentResponseDTO(com.logiflow.api.LogiFlow.model.Shipment shipment) {
        this.id = shipment.getId();
        this.trackingCode = shipment.getTrackingCode();
        this.originCity = shipment.getOriginCity();
        this.destinationCity = shipment.getDestinationCity();
        this.distanceKm = shipment.getDistanceKm();
        this.freightValue = shipment.getFreightValue() != null
                ? shipment.getFreightValue().setScale(2, RoundingMode.HALF_UP)
                : null;
    }
}