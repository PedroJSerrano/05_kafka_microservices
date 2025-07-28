package pjserrano.shipments.model;

import lombok.Getter;

@Getter
public enum ShipmentStatus {

    PENDING("PENDING"),
    CANCELLED("CANCELLED"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    UNDELIVERED("UNDELIVERED"),
    RETURNED("RETURNED");

    private final String status;

    ShipmentStatus(String status) {
        this.status = status;
    }
}
