package com.pjserrano.shipments.controller;

import com.pjserrano.shipments.model.ShipmentStatus;
import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.service.IShipmentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShipmentsControllerTest {

    @InjectMocks
    private ShipmentsController shipmentsController;

    @Mock
    private IShipmentsService shipmentsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
