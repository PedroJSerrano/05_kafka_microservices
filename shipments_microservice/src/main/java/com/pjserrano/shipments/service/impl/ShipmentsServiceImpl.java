package com.pjserrano.shipments.service.impl;

import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.repository.ShipmentsRepository;
import com.pjserrano.shipments.service.IShipmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
}
