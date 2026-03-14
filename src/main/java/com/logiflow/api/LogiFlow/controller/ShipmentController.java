package com.logiflow.api.LogiFlow.controller;

import com.logiflow.api.LogiFlow.dto.ShipmentResponseDTO;
import com.logiflow.api.LogiFlow.model.Shipment;
import com.logiflow.api.LogiFlow.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    @Autowired
    private ShipmentService service;

    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> criar(@Valid @RequestBody Shipment shipment) {
        log.info("Requisição POST /api/shipments recebida");
        Shipment novoEnvio = service.processarNovoEnvio(shipment);
        ShipmentResponseDTO response = new ShipmentResponseDTO(novoEnvio);
        log.info("Envio criado com sucesso. Tracking: {}", novoEnvio.getTrackingCode());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public List<ShipmentResponseDTO> listar() {
        log.info("Requisição GET /api/shipments recebida");
        return service.buscarTodos().stream()
                .map(ShipmentResponseDTO::new)
                .collect(Collectors.toList()); // Mais seguro para versões hibridas de Java
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ShipmentResponseDTO>> listarRecentes() {
        log.info("Requisição GET /api/shipments/recent recebida");
        return ResponseEntity.ok(service.buscarUltimos10());
    }
}