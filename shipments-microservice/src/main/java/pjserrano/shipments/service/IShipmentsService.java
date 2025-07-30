package pjserrano.shipments.service;

import pjserrano.shipments.model.Shipments;
import reactor.core.publisher.Flux;

public interface IShipmentsService {

    public Flux<Shipments> getShipments();
    public Flux<Shipments> getPendingShipments();
    public Flux<Shipments> getShipmentsByStatus(String status);
}
