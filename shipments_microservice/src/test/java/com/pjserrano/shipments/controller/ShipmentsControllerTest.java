package com.pjserrano.shipments.controller;

import com.pjserrano.shipments.model.ShipmentStatus;
import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.service.IShipmentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShipmentsControllerTest {

    @InjectMocks
    private ShipmentsController shipmentsController;

    @Mock
    private IShipmentsService shipmentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getShipments() {
        Shipments shipment1 = new Shipments(1, 1, LocalDateTime.now(), "Direccion", ShipmentStatus.PENDING.getStatus(), true);
        Shipments shipment2 = new Shipments(2, 2, LocalDateTime.now(), "Direccion", ShipmentStatus.SHIPPED.getStatus(), true);
        Shipments shipment3 = new Shipments(2, 2, LocalDateTime.now(), "Direccion", ShipmentStatus.DELIVERED.getStatus(), true);

        when(shipmentsService.getShipments()).thenReturn(Flux.just(shipment1, shipment2, shipment3));

        ResponseEntity<Flux<Shipments>> res = shipmentsController.getShipments();

        StepVerifier.create(res.getBody())
                .expectNext(shipment1)
                .expectNext(shipment2)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsService).getShipments();
    }

    @Test
    void getPendingShipments() {
        Shipments shipment1 = new Shipments(1, 1, LocalDateTime.now(), "Direccion", ShipmentStatus.PENDING.getStatus(), true);
        Shipments shipment2 = new Shipments(2, 2, LocalDateTime.now(), "Direccion", ShipmentStatus.PENDING.getStatus(), true);

        when(shipmentsService.getShipments()).thenReturn(Flux.just(shipment1, shipment2));

        ResponseEntity<Flux<Shipments>> res = shipmentsController.getPendingShipments();

        StepVerifier.create(res.getBody())
                .expectNext(shipment1)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsService).getShipments();
    }

    @Test
    void getShipmentsByStatus() {
        Shipments shipment1 = new Shipments(1, 1, LocalDateTime.now(), "Direccion", ShipmentStatus.SHIPPED.getStatus(), true);
        Shipments shipment2 = new Shipments(2, 2, LocalDateTime.now(), "Direccion", ShipmentStatus.SHIPPED.getStatus(), true);

        when(shipmentsService.getShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus())).thenReturn(Flux.just(shipment1, shipment2));

        ResponseEntity<Flux<Shipments>> res = shipmentsController.getShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus());

        StepVerifier.create(res.getBody())
                .expectNext(shipment1)
                .expectNextCount(1)
                .verifyComplete();

        verify(shipmentsService).getShipmentsByStatus(ShipmentStatus.SHIPPED.getStatus());
    }
}
