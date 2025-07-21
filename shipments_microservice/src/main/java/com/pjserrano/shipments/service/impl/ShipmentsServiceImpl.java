package com.pjserrano.shipments.service.impl;

import com.pjserrano.shipments.model.ShipmentStatus;
import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.repository.ShipmentsRepository;
import com.pjserrano.shipments.service.IShipmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pjserrano.common.model.MyOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ShipmentsServiceImpl implements IShipmentsService {

    private final ShipmentsRepository shipmentsRepository;

    @Autowired
    public ShipmentsServiceImpl(ShipmentsRepository shipmentsRepository) {
        this.shipmentsRepository = shipmentsRepository;
    }

    @Override
    public Flux<Shipments> getShipments() {
        return this.shipmentsRepository.findAll();
    }

    @Override
    public Flux<Shipments> getPendingShipments() {
        return this.shipmentsRepository.findPendingShipments();
    }

    @Override
    public Flux<Shipments> getShipmentsByStatus(String status) {
        return this.shipmentsRepository.findShipmentsByStatus(status);
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "group2")
    public Mono<Void> saveShipments(MyOrder order) {
        return Mono.just(order)
                .map(this::mapOrderToShipment)
                .flatMap(this.shipmentsRepository::save)
                .then();
    }

    private Shipments mapOrderToShipment(MyOrder order) {
        return new Shipments(
                null,
                order.getCodeProductOrdered(),
                LocalDateTime.now(),
                order.getAddress(),
                ShipmentStatus.PENDING.getStatus(),
                true);
    }
}
