package com.logiflow.api.LogiFlow.service;

import com.logiflow.api.LogiFlow.dto.NominatimResponseDTO;
import com.logiflow.api.LogiFlow.dto.RouteResponseDTO;
import com.logiflow.api.LogiFlow.dto.ShipmentResponseDTO;
import com.logiflow.api.LogiFlow.model.Shipment;
import com.logiflow.api.LogiFlow.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final RestTemplate restTemplate;
    private final ShipmentRepository repository;

    private static final Double FATOR_CUBAGEM = 300.0;

    private double[] geocodificarCidade(String cidade, String estado) {
        if (cidade == null || cidade.isBlank()) {
            log.warn("Nome de cidade inválido para geocodificação.");
            return null;
        }
        try {
            String query = (estado != null && !estado.isBlank()) ? cidade + ", " + estado : cidade;
            String queryCodificada = UriUtils.encodeQueryParam(query, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + queryCodificada + "&format=json&limit=1&countrycodes=br";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "LogiFlow/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            NominatimResponseDTO[] response = restTemplate.exchange(url, HttpMethod.GET, entity, NominatimResponseDTO[].class).getBody();
            if (response != null && response.length > 0) {
                double lat = Double.parseDouble(response[0].getLat());
                double lon = Double.parseDouble(response[0].getLon());
                log.info("Coordenadas de '{}': lat={}, lon={}", cidade, lat, lon);
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            log.error("Erro ao geocodificar cidade '{}': {}", cidade, e.getMessage());
        }
        return null;
    }

    public Shipment processarNovoEnvio(Shipment shipment) {
        if (shipment.getWeight() == null || shipment.getWeight() <= 0) {
            throw new IllegalArgumentException("Peso deve ser maior que zero");
        }

        shipment.setTrackingCode("LF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        log.info("Processando novo envio com tracking code: {}", shipment.getTrackingCode());

        double[] origem = geocodificarCidade(shipment.getOriginCity(), shipment.getOriginState());
        double[] destino = geocodificarCidade(shipment.getDestinationCity(), shipment.getDestinationState());

        if (origem != null && destino != null) {
            String url = String.format("http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                    origem[1], origem[0], destino[1], destino[0]);
            log.info("Consultando rota na API OSRM: {}", url);
            try {
                RouteResponseDTO response = restTemplate.getForObject(url, RouteResponseDTO.class);
                if (response != null && response.getRoutes() != null && !response.getRoutes().isEmpty()) {
                    Double distanciaMetros = response.getRoutes().get(0).getDistance();
                    shipment.setDistanceKm(distanciaMetros / 1000.0);
                    log.info("Distância calculada pela API: {} km", shipment.getDistanceKm());
                } else {
                    log.warn("API OSRM retornou resposta vazia. Usando distância padrão: 20 km");
                    shipment.setDistanceKm(20.0);
                }
            } catch (Exception e) {
                log.error("Erro ao consultar API OSRM: {}. Usando distância padrão: 20 km", e.getMessage());
                shipment.setDistanceKm(20.0);
            }
        } else {
            log.warn("Não foi possível geocodificar as cidades. Usando distância padrão: 20 km");
            shipment.setDistanceKm(20.0);
        }

        Double volumeM3 = (shipment.getLength() * shipment.getWidth() * shipment.getHeight()) / 1_000_000.0;
        Double pesoCubado = volumeM3 * FATOR_CUBAGEM;
        Double pesoFinal = Math.max(shipment.getWeight(), pesoCubado);
        log.info("Peso real: {} kg | Peso cubado: {} kg | Peso final: {} kg", shipment.getWeight(), pesoCubado, pesoFinal);

        BigDecimal valorPorPeso = BigDecimal.valueOf(pesoFinal * 4.50);
        BigDecimal valorPorDistancia = BigDecimal.valueOf(shipment.getDistanceKm() * 1.20);
        shipment.setFreightValue(valorPorPeso.add(valorPorDistancia));
        log.info("Frete calculado: R$ {} (peso: R$ {} + distância: R$ {})", shipment.getFreightValue(), valorPorPeso, valorPorDistancia);

        return repository.save(shipment);
    }

    public Page<ShipmentResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ShipmentResponseDTO::new);
    }

    public List<ShipmentResponseDTO> buscarUltimos10() {
        return repository.findTop10ByOrderByIdDesc()
                .stream()
                .map(ShipmentResponseDTO::new)
                .collect(Collectors.toList());
    }
}
