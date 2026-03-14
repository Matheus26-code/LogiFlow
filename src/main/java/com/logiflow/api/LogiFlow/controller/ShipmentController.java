package com.logiflow.api.LogiFlow.controller;

import com.logiflow.api.LogiFlow.dto.ShipmentRequestDTO;
import com.logiflow.api.LogiFlow.dto.ShipmentResponseDTO;
import com.logiflow.api.LogiFlow.model.Shipment;
import com.logiflow.api.LogiFlow.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService service;

    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> criar(@Valid @RequestBody ShipmentRequestDTO dto) {
        log.info("Requisição POST /api/shipments recebida");
        Shipment shipment = new Shipment();
        shipment.setOriginCity(dto.getOriginCity());
        shipment.setOriginState(dto.getOriginState());
        shipment.setDestinationCity(dto.getDestinationCity());
        shipment.setDestinationState(dto.getDestinationState());
        shipment.setWeight(dto.getWeight());
        shipment.setLength(dto.getLength());
        shipment.setWidth(dto.getWidth());
        shipment.setHeight(dto.getHeight());
        Shipment novoEnvio = service.processarNovoEnvio(shipment);
        ShipmentResponseDTO response = new ShipmentResponseDTO(novoEnvio);
        log.info("Envio criado com sucesso. Tracking: {}", novoEnvio.getTrackingCode());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public Page<ShipmentResponseDTO> listar(Pageable pageable) {
        log.info("Requisição GET /api/shipments recebida");
        return service.buscarTodos(pageable);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ShipmentResponseDTO>> listarRecentes() {
        log.info("Requisição GET /api/shipments/recent recebida");
        return ResponseEntity.ok(service.buscarUltimos10());
    }
}
