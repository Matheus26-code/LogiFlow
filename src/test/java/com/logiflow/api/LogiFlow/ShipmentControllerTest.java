package com.logiflow.api.LogiFlow;

import com.logiflow.api.LogiFlow.controller.ShipmentController;
import com.logiflow.api.LogiFlow.dto.ShipmentResponseDTO;
import com.logiflow.api.LogiFlow.model.Shipment;
import com.logiflow.api.LogiFlow.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShipmentController.class)
public class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShipmentService service;

    @Test
    public void deveRetornar201QuandoCriarComBodyValido() throws Exception {
        Shipment saved = new Shipment();
        saved.setId(1L);
        saved.setTrackingCode("LF-ABCD1234");
        saved.setOriginCity("Porto Alegre");
        saved.setDestinationCity("São Paulo");
        saved.setDistanceKm(1100.0);
        saved.setFreightValue(new BigDecimal("1488.75"));

        when(service.processarNovoEnvio(any(Shipment.class))).thenReturn(saved);
        when(service.buscarTodos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new ShipmentResponseDTO(saved))));

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originCity\":\"Porto Alegre\",\"originState\":\"Rio Grande do Sul\",\"destinationCity\":\"São Paulo\",\"destinationState\":\"São Paulo\",\"weight\":10.0,\"length\":50.0,\"width\":50.0,\"height\":50.0}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void deveRetornar400QuandoCriarComBodyInvalido() throws Exception {
        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weight\":10.0}"))
                .andExpect(status().isBadRequest());
    }
}
