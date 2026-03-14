package com.logiflow.api.LogiFlow;

import com.logiflow.api.LogiFlow.dto.NominatimResponseDTO;
import com.logiflow.api.LogiFlow.dto.RouteResponseDTO;
import com.logiflow.api.LogiFlow.model.Shipment;
import com.logiflow.api.LogiFlow.repository.ShipmentRepository;
import com.logiflow.api.LogiFlow.service.ShipmentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ShipmentRepository repository;

    @InjectMocks
    private ShipmentService service;

    @Test
    public void deveCalcularFreteComSucesso() {
        // Cenário: caixa 50x50x50cm, 10kg, Porto Alegre → São Paulo
        Shipment shipment = new Shipment();
        shipment.setWeight(10.0);
        shipment.setLength(50.0);
        shipment.setWidth(50.0);
        shipment.setHeight(50.0);
        shipment.setOriginCity("Porto Alegre");
        shipment.setDestinationCity("São Paulo");

        // Mock Nominatim (geocoding)
        NominatimResponseDTO coordOrigem = new NominatimResponseDTO();
        coordOrigem.setLat("-30.0277");
        coordOrigem.setLon("-51.2287");

        NominatimResponseDTO coordDestino = new NominatimResponseDTO();
        coordDestino.setLat("-23.5505");
        coordDestino.setLon("-46.6333");

        when(restTemplate.exchange(contains("Porto%20Alegre"), any(), any(), eq(NominatimResponseDTO[].class)))
                .thenReturn(ResponseEntity.ok(new NominatimResponseDTO[]{coordOrigem}));
        when(restTemplate.exchange(contains("S%C3%A3o%20Paulo"), any(), any(), eq(NominatimResponseDTO[].class)))
                .thenReturn(ResponseEntity.ok(new NominatimResponseDTO[]{coordDestino}));

        // Mock OSRM (rota)
        RouteResponseDTO.Route route = new RouteResponseDTO.Route();
        route.setDistance(1100000.0); // 1100 km
        RouteResponseDTO routeResponse = new RouteResponseDTO();
        routeResponse.setRoutes(List.of(route));
        when(restTemplate.getForObject(contains("osrm"), eq(RouteResponseDTO.class)))
                .thenReturn(routeResponse);

        // Mock save
        when(repository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        // Ação
        Shipment resultado = service.processarNovoEnvio(shipment);

        // Validação
        Assertions.assertNotNull(resultado.getTrackingCode());
        Assertions.assertTrue(resultado.getTrackingCode().startsWith("LF-"));
        // peso cubado: (50*50*50/1_000_000) * 300 = 37.5 kg > 10 kg real
        // frete: 37.5 * 4.50 + 1100 * 1.20 = 168.75 + 1320.00 = 1488.75
        Assertions.assertEquals(new BigDecimal("1488.75"), resultado.getFreightValue().setScale(2, java.math.RoundingMode.HALF_UP));
        Assertions.assertEquals(1100.0, resultado.getDistanceKm());
    }
}
