package com.pjserrano.shipments.service.impl;

import com.pjserrano.shipments.model.ShipmentStatus;
import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.repository.ShipmentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShipmentsServiceImplTest {

    @InjectMocks
    private ShipmentsServiceImpl shipmentsService;

    @Mock
    private ShipmentsRepository shipmentsRepository;

    private Shipments shipment1;
    private Shipments shipment2;
    private Shipments shipment3;

    @BeforeEach
    public void setUp() {
        shipment1 = new Shipments(1, 1, LocalDateTime.now(), "Direccion", ShipmentStatus.PENDING.getStatus(), true);
        shipment2 = new Shipments(2, 2, LocalDateTime.now(), "Direccion", ShipmentStatus.PENDING.getStatus(), true);
        shipment3 = new Shipments(3, 3, LocalDateTime.now(), "Direccion", ShipmentStatus.SHIPPED.getStatus(), true);
    }

    @Test
    void getShipments() {
        when(shipmentsRepository.findAll()).thenReturn(Flux.just(shipment1, shipment2, shipment3));

        StepVerifier.create(shipmentsService.getShipments())
                .expectNext(shipment1, shipment2)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsRepository).findAll();
    }

    @Test
    public void getPendingShipments() {
        when(shipmentsRepository.findPendingShipments()).thenReturn(Flux.just(shipment1, shipment2));

        StepVerifier.create(shipmentsService.getPendingShipments())
                .expectNext(shipment1)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsRepository).findPendingShipments();
    }
}