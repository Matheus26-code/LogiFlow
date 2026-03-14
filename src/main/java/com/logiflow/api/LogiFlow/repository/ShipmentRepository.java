package com.logiflow.api.LogiFlow.repository;

import com.logiflow.api.LogiFlow.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findTop10ByOrderByIdDesc();
}