package com.pjserrano.shipments.controller;

import com.pjserrano.shipments.model.Shipments;
import com.pjserrano.shipments.service.IShipmentsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/shipments")
public class ShipmentsController {

    private final IShipmentsService shipmentsService;

    public ShipmentsController(IShipmentsService shipmentsService) {
        this.shipmentsService = shipmentsService;
    }

    @GetMapping(value = "/pending", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<Shipments>> getPendingShipments() {
        return ResponseEntity.ok(shipmentsService.getPendingShipments());
    }
}
