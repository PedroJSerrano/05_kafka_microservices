package com.pjserrano.shipments.service;

import com.pjserrano.shipments.model.Shipments;
import reactor.core.publisher.Flux;

public interface IShipmentsService {

    public Flux<Shipments> getShipments();
    public Flux<Shipments> getPendingShipments();
    public Flux<Shipments> getShipmentsByStatus(String status);
}
