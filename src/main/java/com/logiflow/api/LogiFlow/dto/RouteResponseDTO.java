package com.logiflow.api.LogiFlow.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteResponseDTO {
    private List<Route> routes;

    @Data
    public static class Route {
        private Double distance;
    }
}