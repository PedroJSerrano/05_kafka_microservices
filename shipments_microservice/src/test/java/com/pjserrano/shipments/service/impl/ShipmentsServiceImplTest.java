package com.pjserrano.shipments.service.impl;

import com.pjserrano.shipments.model.ShipmentStatus;
import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.repository.ShipmentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pjserrano.common.model.MyOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentsServiceImplTest {

    @InjectMocks
    private ShipmentsServiceImpl shipmentsService;

    @Mock
    private ShipmentsRepository shipmentsRepository;

    private Shipments shipment1;
    private Shipments shipment2;
    private Shipments shipment3;
    private MyOrder testOrder;

    @BeforeEach
    void setUp() {
        shipment1 = new Shipments(1, 1, LocalDateTime.now(), "Address 1", ShipmentStatus.PENDING.getStatus(), true);
        shipment2 = new Shipments(2, 2, LocalDateTime.now(), "Address 2", ShipmentStatus.PENDING.getStatus(), true);
        shipment3 = new Shipments(3, 3, LocalDateTime.now(), "Address 3", ShipmentStatus.SHIPPED.getStatus(), true);

        testOrder = new MyOrder(1, "ProductA", 10, "Address1");
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
    void getPendingShipments() {
        when(shipmentsRepository.findPendingShipments()).thenReturn(Flux.just(shipment1, shipment2));

        StepVerifier.create(shipmentsService.getPendingShipments())
                .expectNext(shipment1)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsRepository).findPendingShipments();
    }

    @Test
    void getShipmentsByStatus_shipped() {
        when(shipmentsRepository.findShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus())).thenReturn(Flux.just(shipment3));
        StepVerifier.create(shipmentsService.getShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus()))
                .expectNext(shipment3)
                .verifyComplete();
        verify(shipmentsRepository).findShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus());
    }

    @Test
    void saveShipments_shouldMapAndSaveOrder() {
        when(shipmentsRepository.save(any(Shipments.class)))
                .thenReturn(Mono.just(new Shipments(
                        1,
                        testOrder.getCodeProductOrdered(),
                        LocalDateTime.now(),
                        testOrder.getAddress(),
                        ShipmentStatus.PENDING.getStatus(),
                        true
                )));

        Mono<Void> resultMono = shipmentsService.saveShipments(testOrder);

        StepVerifier.create(resultMono)
                .verifyComplete();

        ArgumentCaptor<Shipments> shipmentCaptor = ArgumentCaptor.forClass(Shipments.class);
        verify(shipmentsRepository).save(shipmentCaptor.capture());
    }
}