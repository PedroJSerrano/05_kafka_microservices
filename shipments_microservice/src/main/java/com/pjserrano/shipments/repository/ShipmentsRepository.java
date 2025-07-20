package com.pjserrano.shipments.repository;

import com.pjserrano.shipments.model.Shipments;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ShipmentsRepository extends ReactiveCrudRepository<Shipments, Integer> {

    @Query("SELECT * FROM shipments WHERE status = 'PENDING'")
    Flux<Shipments> findPendingShipments();
}
